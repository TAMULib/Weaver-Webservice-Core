package edu.tamu.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import edu.tamu.framework.SpringContext;
import edu.tamu.framework.model.BaseEntity;
import edu.tamu.framework.model.ValidatingBase;
import edu.tamu.framework.validation.BusinessValidator;
import edu.tamu.framework.validation.InputValidator;
import edu.tamu.framework.validation.MethodValidator;
import edu.tamu.framework.validation.ValidationResults;

@Service
public class ValidationUtility {
    
    // TODO: improve regex accordingly
    
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    public static final String INTEGER_REGEX = "^\\d+$";
    public static final String MONTH_REGEX = "^(1[0-1]|[0-9])$";
    
    private static final String ID_COLUMN_NAME = "id";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String PASSWORD_COLUMN_NAME = "password";
    private static final String POSITION_COLUMN_NAME = "position";    
    private static final String SYSTEM_COLUMN_NAME = "isSystemRequired";
    
    private static final String BUSINESS_MESSAGE_KEY = "business";
    private static final String METHOD_MESSAGE_KEY = "method";
    
    

    public static <U extends ValidatingBase> ValidationResults validateInputs(InputValidator validator, U model) {
        ValidationResults results = new ValidationResults();

        Object value = getValueForProperty(model, validator.getProperty());

        switch (validator.getType()) {
            case minlength: {
    
                if (value != null) {
                    if (((String) value).length() < (Integer) validator.getValue()) {
                    	results.addMessage(validator.getProperty(), validator.getType().toString(), validator.getMessage());
                        results.setValid(false);
                    }
                }
    
            } break;
            case maxlength: {
    
                if (value != null) {
                    if (((String) value).length() > (Integer) validator.getValue()) {
                    	results.addMessage(validator.getProperty(), validator.getType().toString(), validator.getMessage());
                        results.setValid(false);
                    }
                }
    
            } break;
            case required: {
                // TODO: improve how to ignore JsonIgnored properties
                if (value == null && !validator.getProperty().equals(PASSWORD_COLUMN_NAME)) {
                    results.addMessage(validator.getProperty(), validator.getType().toString(), validator.getMessage());
                    results.setValid(false);
                }
    
            } break;
            case pattern: {
    
                if (value != null) {
                    Pattern pattern = Pattern.compile((String) validator.getValue());
                    
                    String valueString = null;
                    if(value instanceof Integer) {
                        valueString = Integer.toString((Integer) value);
                    }
                    else if(value instanceof Long) {
                        valueString = Long.toString((Long) value);
                    }
                    else {
                        valueString = (String) value;
                    }
                    
                    Matcher matcher = pattern.matcher(valueString);
                    if (!matcher.matches()) {
                    	results.addMessage(validator.getProperty(), validator.getType().toString(), validator.getMessage());
                        results.setValid(false);
                    }
                }
    
            } break;
            default: {
                
            } break;
        }

        return results;
    }

    @SuppressWarnings("unchecked")
    public static <U extends ValidatingBase> ValidationResults validateBusiness(BusinessValidator validator, U model) {
        ValidationResults results = new ValidationResults();

        switch (validator.getType()) {
            case CREATE: {
    
                // check if unique constraints will be violated!!
                
                UniqueConstraintViolation uniqueConstraintViolation = validateUniqueConstraints(model);
                
                if (uniqueConstraintViolation.invalid) {
                    results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), uniqueConstraintViolation.message);
                    results.setValid(false);
                }
    
            } break;
            case READ: {
            	
            	// check if requestor's role matches that of a param(roles)
                
            } break;
            case UPDATE: {
            	
            	boolean invalid = false;
    
            	String message = null;
            	
                // check if model does not exist
                
            	Long id = ((BaseEntity) model).getId();
    
                if (id != null) {
                    if (queryById(model, id).size() == 0) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " with id " + id + " does not exist";
                    }
                }
                
                if(!invalid) {
                    
                    // check if unique constraints will be violated!!
                        
                    Boolean isSystemRequired = (Boolean) getValueForProperty(model, SYSTEM_COLUMN_NAME);
                    
                    if(isSystemRequired != null) {                                             
                        if(isSystemRequired) {
                            model = createNewFromSystemDefault(model);
                        }
                    }
                    else {
                        if(uniqueConstraintPropertyChange(model)) {
                            UniqueConstraintViolation uniqueConstraintViolation = validateUniqueConstraints(model);
                            invalid = uniqueConstraintViolation.invalid;
                            message = uniqueConstraintViolation.message;
                        }
                    }
                }
                
                if (invalid) {
                    results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
    
            } break;
            case DELETE: {
            	
            	boolean invalid = false;
                
            	String message = null;
            	
            	// check if model does not exist
                
            	Long id = ((BaseEntity) model).getId();
                
                List<Object> queryResults = new ArrayList<Object>();
    
                if (id != null) {
                	queryResults = queryById(model, id);
                    if (queryResults.size() == 0) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " with id " + id + " does not exist";
                    }
                }
                
                if(!invalid) {
                    
                    // check if relationships from params(columns) will cause foreign key constraints!!
                	
                	U modelToDelete = (U) queryResults.get(0);
                	
                	for(String param : validator.getParams()) {
                		
                		Object relation = getValueForProperty(modelToDelete, param);
                		
                		if(relation instanceof Set) {
                			if(((Set<Object>) relation).size() > 0) {
                				invalid = true;
                				
                				String name = (String) getValueForProperty(model, NAME_COLUMN_NAME);
                				
                				if(name != null) {
                				    message = "Could not delete " + model.getClass().getSimpleName() + " " + name + "(" + ((BaseEntity) model).getId() + ") due to having " + param; 
                				}
                				else {
                				    message = "Could not delete " + model.getClass().getSimpleName() + " with id " + ((BaseEntity) model).getId() + " due to having " + param; 
                				}
                				
                			}
                		}
                		else if(relation instanceof List) {
                			if(((List<Object>) relation).size() > 0) {
                				invalid = true;
                				
                				String name = (String) getValueForProperty(model, NAME_COLUMN_NAME);
                                
                                if(name != null) {
                                    message = "Could not delete " + model.getClass().getSimpleName() + " " + name + "(" + ((BaseEntity) model).getId() + ") due to having " + param; 
                                }
                                else {
                                    message = "Could not delete " + model.getClass().getSimpleName() + " with id " + ((BaseEntity) model).getId() + " due to having " + param; 
                                }
                			}
                		}
                		else {
 
                		}
                		
                	}
                	
                	if(!invalid) {
                	
	                	// check if relationships from joins(owners) will cause foreign key constraints!!
                		
                		// TODO: make message aggregate of all owning entities
                		
	                	for(Class<?> join : validator.getJoins()) {
	                		
	                		if(!invalid) {
	                			
	                			for(Field field : join.getDeclaredFields()) {
		                			
		                			if(field.getType().equals(modelToDelete.getClass())) {
		                				
		                				List<Object> queryByPropertyResults = queryByProperty(join, field.getName(), modelToDelete);
		                				
	                        			if(queryByPropertyResults.size() > 0) {
	                        				invalid = true;
	                        				
	                        				U owningModel = (U) queryByPropertyResults.get(0);
	                        				
                        				    message = "Could not delete " + modelToDelete.getClass().getSimpleName() + " with id " + ((BaseEntity) modelToDelete).getId() + " due to being used by " + owningModel.getClass().getSimpleName() + " with id " + ((BaseEntity) owningModel).getId();
	                        				
	                        			}
		                        				                				
		                			}
		                		}
	                		}	                		
	                	}                	
                	}                	
                }
                
                if(!invalid) {
                    Object value = getValueForProperty(model, SYSTEM_COLUMN_NAME);
                    if(value != null && ((Boolean) value) == true) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " with id " + ((BaseEntity) model).getId() + " is a system default and cannot be deleted";
                    }
                }
                
                if (invalid) {
                    results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
                
            } break;
            case EXISTS: {
                
                // check if model exists
                
                Long id = ((BaseEntity) model).getId();
    
                if (id != null) {
                    if (queryById(model, id).size() != 0) {
                        String message = model.getClass().getSimpleName() + " with id " + id + " already exists";
                        results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), message);
                        results.setValid(false);
                    }
                }
    
            } break;
            case NONEXISTS: {
    
                // check if model does not exist
                
                Long id = ((BaseEntity) model).getId();
    
                if (id != null) {
                    if (queryById(model, id).size() == 0) {
                        String message = model.getClass().getSimpleName() + " with id " + id + " does not exist";
                        results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), message);
                        results.setValid(false);
                    }
                }
    
            } break;
            case RESET: {
                
                boolean invalid = false;
                
                String message = null;
                
                // check if model exists
                
                Long id = ((BaseEntity) model).getId();
                
                if (id != null) {
                    if (queryById(model, id).size() == 0) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " with id " + id + " does not exist";
                    }
                }
                
                // check if model has a system default
                
                if(!invalid) {
                    if(getFieldForProperty(model, SYSTEM_COLUMN_NAME) == null) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " is not a system default in which can be reset";
                    }
                }
                
                // check if model is not a system default
                
                if(!invalid) {
                    if((Boolean) getValueForProperty(model, SYSTEM_COLUMN_NAME) == true) {
                        invalid = true;
                        message = model.getClass().getSimpleName() + " is the system default";
                    }
                }
                
                if (invalid) {
                    results.addMessage(BUSINESS_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
    
            } break;
            default: {
    
            } break;
        }

        return results;
    }
    
    @SuppressWarnings("unchecked")
    public static <U extends ValidatingBase> ValidationResults validateMethod(MethodValidator validator) {
        ValidationResults results = new ValidationResults();
        
        switch (validator.getType()) {
            
            case REORDER: {
            	
            	boolean invalid = false;
            	
            	String message = null;
            	
            	// check if positions exist
            	
            	if(validator.getArgs().length < 2) {
                    invalid = true;
                    message = "Endpoint must have at least source and destination of reorder";
                }
            	
            	// check if param 0 is correct index for source
            	
            	Integer srcIndex = null;
            	
            	if(!invalid) {
                	try {
                		srcIndex = Integer.parseInt(validator.getParams()[0]);
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Source endpoint argument index is out of range";
                	}
            	}
            	
            	// check if param 1 is correct index for destination
            	
            	Integer destIndex = null;
            	
            	if(!invalid) {
                	try {
                		destIndex = Integer.parseInt(validator.getParams()[1]);
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Destination endpoint argument index is out of range";
                	}
            	}
            	
            	String facet = null;
            	
            	if(!invalid) {   
                	if(validator.getArgs().length > 2) {                	    
                        
                        try {
                            facet = validator.getParams()[2];
                        }
                        catch(Exception e) {
                            invalid = true;
                            message = "Facet not specified";
                        }
                        
                        if(!invalid) {                            
                            if(recursivelyFindField(validator.getClazz(), facet) == null) {
                                invalid = true;
                                message = "Facet not a property of " + validator.getClazz().getSimpleName();
                            }         
                        }
                	}
            	}
            	
                if(!invalid) {                	
                    if(queryByPosition(validator.getClazz(), (Long) validator.getArgs()[srcIndex]).size() == 0) {
                        invalid = true;
                        message = "Source is out of range";
                    }
                }
                    
                if(!invalid) {     
                    if(queryByPosition(validator.getClazz(), (Long) validator.getArgs()[destIndex]).size() == 0) {
                        invalid = true;
                        message = "Destination is out of range";
                    }
                }
            	
            	if (invalid) {
                    results.addMessage(METHOD_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
                
            } break;
            case LIST_REORDER: {
            	
            	boolean invalid = false;
            	
            	String message = null;
            	
            	if(validator.getParams().length < 4) {
            		invalid = true;
                    message = "Method annotation for LIST_REORDER required 4 params";
            	}
            	
            	// check if positions exist
            	
            	if(!invalid) {
	            	if(validator.getArgs().length < 3) {
	                    invalid = true;
	                    message = "Endpoint must have at least owning entity id, source and destination of reorder";
	                }
            	}
            	
            	// check if param 0 is correct index for source
            	
            	Integer srcIndex = null;
            	
            	if(!invalid) { 
                	try {
                		srcIndex = Integer.parseInt(validator.getParams()[0]) - 1;
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Source endpoint argument index is out of range";
                	}
            	}
            	
            	// check if param 1 is correct index for destination
            	
            	Integer destIndex = null;
            	
            	if(!invalid) { 
                	try {
                		destIndex = Integer.parseInt(validator.getParams()[1]) - 1;
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Destination endpoint argument index is out of range";
                	}
            	}
            	
            	// check if param 2 is correct index for owning entity
            	
            	Long ownerId = null;
            	
            	if(!invalid) { 
                	try {
                		ownerId = Long.parseLong(validator.getParams()[2]);
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Owning entity endpoint argument index is out of range";
                	}
            	}
            	            	
            	if(!invalid) {
            	
	            	List<Object> queryResults = queryWithClassById(validator.getClazz(), ownerId);
	            	
	            	if(queryResults.size() > 0) {
	            		U model = (U) queryResults.get(0);
	            		
	            		Object relation = getValueForProperty(model, validator.getParams()[3]);
	            		
	            		if(relation instanceof Set) {
	            			
	            			try {
	            				@SuppressWarnings("unused")
                                Object srcObj = ((Set<Object>) relation).toArray()[srcIndex];
	            			}
	            			catch(Exception e) {
	            				invalid = true;
                        		message = "Collection does not contain source index";
	            			}
	            			
	            			if(!invalid) {
		            			try {
		            			    @SuppressWarnings("unused")
		            				Object destObj = ((Set<Object>) relation).toArray()[destIndex];
		            			}
		            			catch(Exception e) {
		            				invalid = true;
	                        		message = "Collection does not contain destination index";
		            			}
	            			}
	            			
                		}
                		else if(relation instanceof List) {
                			
                			try {
                			    @SuppressWarnings("unused")
	            				Object srcObj = ((List<Object>) relation).get(srcIndex);
	            			}
	            			catch(Exception e) {
	            				invalid = true;
                        		message = "Collection does not contain source index";
	            			}
	            			
	            			if(!invalid) {
		            			try {
		            			    @SuppressWarnings("unused")
		            				Object destObj = ((List<Object>) relation).get(destIndex);
		            			}
		            			catch(Exception e) {
		            				invalid = true;
	                        		message = "Collection does not contain destination index";
		            			}
	            			}
                		}
                		else {
 
                		}
	            	}
	            	else {
	            		invalid = true;
	            		message = "Cannot find " + validator.getClazz().getSimpleName() + " with id " + ownerId;
	            	}
            	}
            	
            	if (invalid) {
                    results.addMessage(METHOD_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
                
            } break;
            case SORT: {
            	
            	boolean invalid = false;
            	
            	String message = null;
            	
            	// check if column exists
            	
            	if(validator.getArgs().length < 1) {
                    invalid = true;
                    message = "Endpoint must have at least column to sort by";
                }
            	
            	// check if param 0 is correct index for column
            	
            	Integer columnIndex = null;
            	
            	if(!invalid) {
                	try {
                		columnIndex = Integer.parseInt(validator.getParams()[0]);
                	}
                	catch(Exception e) {
                		invalid = true;
                		message = "Column endpoint argument index is out of range";
                	}
            	}
            	
            	if(!invalid) {   
                    if(recursivelyFindField(validator.getClazz(), (String) validator.getArgs()[columnIndex]) == null) {
                        invalid = true;
                        message = (String) validator.getArgs()[0] + " is not a column of " + validator.getClazz().getSimpleName();
                    }
                }
            	
            	Integer facetIndex = null;
                
            	if(!invalid) {   
                    if(validator.getArgs().length > 1) {
                                            
                        try {
                            facetIndex = Integer.parseInt(validator.getParams()[1]);
                        }
                        catch(Exception e) {
                            invalid = true;
                            message = "Facet endpoint argument index is out of range";
                        }
                    }  
            	}
            	
            	if(!invalid && facetIndex != null) {
            	    if(validator.getArgs()[facetIndex] == null) {
            	        invalid = true;
                        message = "Facet endpoint argument is null";
            	    }
            	}
            	
            	if(!invalid && facetIndex != null) {
                	if(recursivelyFindField(validator.getClazz(), validator.getParams()[2]) == null) {
                        invalid = true;
                        message = "Facet not a property of " + validator.getClazz().getSimpleName();
                    }
            	}
            	
            	if (invalid) {
                    results.addMessage(METHOD_MESSAGE_KEY, validator.getType().toString(), message);
                    results.setValid(false);
                }
                
            } break;
            default: {
    
            } break;
        }

        return results;
    }
    
    @SuppressWarnings("unchecked")
    private static <U extends ValidatingBase> Boolean uniqueConstraintPropertyChange(U model) {
        
        Boolean change = false;
        
        Long id = ((BaseEntity) model).getId();
        
        if(id != null) {
        
            U persistedModel = (U) queryById(model, id).get(0);
            
            if(persistedModel != null) {
            
                List<String> uniqueColumns = getUniqueConstraints(model);
                
                for (String property : uniqueColumns) {
                    
                    Object value = getValueForProperty(model, property);
                    Object persistedValue = getValueForProperty(persistedModel, property);
                    
                    if((value == null && persistedValue != null) || (value != null && persistedValue == null)) {
                        change = true;
                    }
                    
                    if((value != null && persistedValue != null)) {                        
                        if(!value.equals(persistedValue)) {
                            change = true;
                        }
                    }
                }
            }
        }
        
        return change;
    }
    
    private static <U extends ValidatingBase> UniqueConstraintViolation validateUniqueConstraints(U model) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(model.getClass());

        List<Predicate> predicates = new ArrayList<Predicate>();
        
        List<String> uniqueColumns = getUniqueConstraints(model);
        
        UniqueConstraintViolation uniqueConstraintViolation = new UniqueConstraintViolation();
        
        boolean invalid = false;
        
        uniqueConstraintViolation.message = model.getClass().getSimpleName() + " has invalid ";

        for (String property : uniqueColumns) {
            
            Object value = getValueForProperty(model, property);
            
            if (value != null) {
            	if(!(value instanceof BaseEntity) || ((value instanceof BaseEntity) && ((BaseEntity) value).getId() != null)) {
            		                    
            	    if(!((value instanceof String) && ((String) value).length() == 0)) {            	        
            	        predicates.add(cb.equal(root.get(property), value));
            	    }
            	   
            	}
            } else {
                
            	// TODO: check if nullable
            	
            }
        }
        
        uniqueConstraintViolation.message = uniqueConstraintViolation.message.substring(0, uniqueConstraintViolation.message.length() - 2);
        int index = uniqueConstraintViolation.message.lastIndexOf(", ");
        if (index >= 0) {
            uniqueConstraintViolation.message = new StringBuilder(uniqueConstraintViolation.message).replace(index, index + ", ".length(), " and ").toString();
        }
        
        if (!invalid) {
            
            if(uniqueColumns.size() > 0 && predicates.size() > 0) {
            
                query.select(root).where(predicates.toArray(new Predicate[] {}));
    
                if (entityManager.createQuery(query).getResultList().size() > 0) {
                    invalid = true;
                    uniqueConstraintViolation.message = craftUniqueConstraintsMessage(model, uniqueColumns);
                }
            }
        }
        
        uniqueConstraintViolation.invalid = invalid;
        
        return uniqueConstraintViolation;
    }
    
    private static <U extends ValidatingBase> String craftUniqueConstraintsMessage(U model, List<String> violatingColumns) {
        String message = model.getClass().getSimpleName() + " with ";
        for(String column : violatingColumns) {
            Object value = getValueForProperty(model, column);
            if(value != null) {
            	message += column + " of " + value + ", ";
            }
        }
        message = message.substring(0, message.length() - 2) + " already exists";
        int index = message.lastIndexOf(", ");
        if (index >= 0) {
            message = new StringBuilder(message).replace(index, index + ", ".length(), " and ").toString();
        }
        return message;
    }
    
    private static <U extends ValidatingBase> List<String> getUniqueConstraints(U model) {
        List<String> uniqueColumns = new ArrayList<String>();
        uniqueColumns.addAll(recursivelyFindTableAnnotation(model.getClass()));
        uniqueColumns.addAll(recursivelyFindUniqueColumn(model.getClass()));        
        return uniqueColumns;
    }
    
    public static List<String> recursivelyFindTableAnnotation(Class<?> clazz) {
    	List<String> uniqueColumns = new ArrayList<String>();
    	
    	// get unique constraints from class Table annotation
        for (Annotation classAnnotation : clazz.getAnnotations()) {
            if (classAnnotation instanceof Table) {
                for (UniqueConstraint uniqueConstraints : ((Table) classAnnotation).uniqueConstraints()) {
                    for (String uniqueColumn : uniqueConstraints.columnNames()) {
                        // is this safe enough?
                    	if(uniqueColumn.endsWith("_id")) {
                            uniqueColumn = uniqueColumn.substring(0, uniqueColumn.length() - 3);
                        }
                        uniqueColumns.add(uniqueColumn);
                    }
                }
            }
        }
    	
        if(clazz.getSuperclass() != null) {
            Class<?> superClazz = clazz.getSuperclass();
            if(superClazz != null) {
            	uniqueColumns.addAll(recursivelyFindTableAnnotation(superClazz));
                return uniqueColumns;
            }
        }
            
        return uniqueColumns;
    }
    
    public static List<String> recursivelyFindUniqueColumn(Class<?> clazz) {
    	List<String> uniqueColumns = new ArrayList<String>();
    	
    	// get unique constraints from member Column annotation
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation memberAnnotation : field.getAnnotations()) {
                if (memberAnnotation instanceof Column) {
                    if (((Column) memberAnnotation).unique()) {
                        uniqueColumns.add(field.getName());
                    }
                }
            }
        }
    	
        if(clazz.getSuperclass() != null) {
            Class<?> superClazz = clazz.getSuperclass();
            if(superClazz != null) {
            	uniqueColumns.addAll(recursivelyFindUniqueColumn(superClazz));
                return uniqueColumns;
            }
        }
            
        return uniqueColumns;
    }
    
    private static <U extends ValidatingBase> Field getFieldForProperty(U model, String property) {        
        return recursivelyFindField(model.getClass(), property);
    }
    
    private static <U extends ValidatingBase> Object getValueForProperty(U model, String property) {
        return getValueForField(model, getFieldForProperty(model, property));
    }
    
    private static <U extends ValidatingBase>  Object getValueForField(U model, Field field) {
    	Object value = null;
        
        if(field != null) {
	        field.setAccessible(true);
	
	        try {
	            value = field.get(model);
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	
	        field.setAccessible(false);
        }
        return value;
    }
    
    private static <U extends ValidatingBase> void setValueForProperty(U model, String property, Object value) {
        Field field = getFieldForProperty(model, property);

        if(field != null) {
	        field.setAccessible(true);
	        
	        try {
	            field.set(model, value);
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        }
	
	        field.setAccessible(false);
        }
        else {
        	// TODO: log error
        }
    }
    
    private static <U extends ValidatingBase> List<Object> queryById(U model, Long id) {        
        return queryWithClassById(model.getClass(), id);
    }
    
    private static <U extends ValidatingBase> List<Object> queryWithClassById(Class<?> clazz, Long id) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(ID_COLUMN_NAME), id));
        return entityManager.createQuery(query).getResultList();
    }
    
    private static <U extends ValidatingBase> List<Object> queryByPosition(Class<?> clazz, Long position) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(POSITION_COLUMN_NAME), position));
        return entityManager.createQuery(query).getResultList();
    }
    
    private static <U extends ValidatingBase> List<Object> queryByProperty(Class<?> clazz, String property, Object value) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(property), value));
        return entityManager.createQuery(query).getResultList();
    }
    
    private static <U extends ValidatingBase> U createNewFromSystemDefault(U model) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        setValueForProperty(model, SYSTEM_COLUMN_NAME, false);   
        setValueForProperty(model, ID_COLUMN_NAME, null);
        entityManager.persist(model);
        return model;
    }
    
    public static Field recursivelyFindField(Class<?> clazz, String property) {
        Field field = null;
        
        try {
            field = clazz.getDeclaredField(property);
        } catch (NoSuchFieldException e) {
            //e.printStackTrace();
        } catch (SecurityException e) {
            //e.printStackTrace();
        }
        
        if(field == null) {
            Class<?> superClazz = clazz.getSuperclass();
            if(superClazz != null) {
                return recursivelyFindField(superClazz, property);
            }
        }
            
        return field;        
    }

    public static void aggregateValidationResults(ValidationResults into, ValidationResults from) {
        for (Entry<String, Map<String, String>> entry : from.getMessages().entrySet()) {
            String key = entry.getKey();
            Map<String, String> messages = entry.getValue();
            for (Entry<String, String> innerEntry : messages.entrySet()) {
                String type = innerEntry.getKey();
                String message = innerEntry.getValue();
                into.addMessage(key, type, message);
            }
        }
        if (into.isValid() && !from.isValid()) {
            into.setValid(from.isValid());
        }
    }
    
    public static class UniqueConstraintViolation {
        public boolean invalid = false;
        public String message;
        public UniqueConstraintViolation() { }
    }

}
