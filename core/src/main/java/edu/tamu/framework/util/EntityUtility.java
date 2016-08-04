package edu.tamu.framework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIdentityReference;

import edu.tamu.framework.SpringContext;
import edu.tamu.framework.model.ValidatingBase;

@Service
public class EntityUtility {
        
    // TODO: add logging!!!
    
    public static final String ID_COLUMN_NAME = "id";
    public static final String NAME_COLUMN_NAME = "name";
    public static final String PASSWORD_COLUMN_NAME = "password";
    public static final String POSITION_COLUMN_NAME = "position";    
    public static final String SYSTEM_COLUMN_NAME = "isSystemRequired";
        
    public static String snakeToCamelWithoutId(String value) {
        int l = 0;
        if(value.endsWith("_id")) {
            value = value.substring(0, value.length() - 3);
        }
        while ((l = value.indexOf("_")) >= 0) {
            value = value.substring(0, l) + String.valueOf(value.charAt(l+1)).toUpperCase() + value.substring(l+2, value.length());
        }
        return value;
    }
    
    public static List<String> recursivelyFindTableAnnotation(Class<?> clazz) {
    	List<String> uniqueColumns = new ArrayList<String>();
    	
    	// get unique constraints from class Table annotation
        for (Annotation classAnnotation : clazz.getAnnotations()) {
            if (classAnnotation instanceof Table) {
                for (UniqueConstraint uniqueConstraints : ((Table) classAnnotation).uniqueConstraints()) {
                    for (String uniqueColumn : uniqueConstraints.columnNames()) {
                    	uniqueColumn = snakeToCamelWithoutId(uniqueColumn);
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
    
    public static Object getValueFromPath(Object model, String[] path) {        
        return recursivelyTraversePath(model, path);
    }
    
    public static Object recursivelyTraversePath(Object object, String[] path) {
        String property = path[0]; 
        if(path.length > 1) {
            path = Arrays.copyOfRange(path, 1, path.length);
            return recursivelyTraversePath(getValueForProperty(object, property), path);
        }        
        return getValueForProperty(object, property);
    }    
    
    public static Field getFieldForProperty(Object model, String property) {        
        return recursivelyFindField(model.getClass(), property);
    }
    
    public static Object getValueForProperty(Object model, String property) {
        return getValueForField(model, getFieldForProperty(model, property));
    }
        
    public static Object getValueForField(Object model, Field field) {
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
    
    public static void setValueForProperty(Object model, String property, Object value) {
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
    
    public static <U extends ValidatingBase> List<Object> queryById(U model, Long id) {        
        return queryWithClassById(model.getClass(), id);
    }
    
    public static <U extends ValidatingBase> List<Object> queryWithClassById(Class<?> clazz, Long id) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(ID_COLUMN_NAME), id));
        return entityManager.createQuery(query).getResultList();
    }
    
    public static <U extends ValidatingBase> List<Object> queryAllWithClass(Class<?> clazz) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root);
        return entityManager.createQuery(query).getResultList();
    }
    
    public static <U extends ValidatingBase> List<Object> queryByPosition(Class<?> clazz, Long position) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(POSITION_COLUMN_NAME), position));
        return entityManager.createQuery(query).getResultList();
    }
    
    public static <U extends ValidatingBase> List<Object> queryByProperty(Class<?> clazz, String property, Object value) {
        EntityManager entityManager = SpringContext.bean(EntityManager.class);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object> query = cb.createQuery();
        Root<?> root = query.from(clazz);
        query.select(root).where(cb.equal(root.get(property), value));
        return entityManager.createQuery(query).getResultList();
    }
    
    public static <U extends ValidatingBase> U createNewFromSystemDefault(U model) {
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
    
    public static List<String> recursivelyFindJsonIdentityReference(Class<?> clazz) {
        List<String> jsonIdentityReferences = new ArrayList<String>();
        
        // find JsonIdentityReference on fields
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation memberAnnotation : field.getAnnotations()) {
                if (memberAnnotation instanceof JsonIdentityReference) {
                    jsonIdentityReferences.add(field.getName());
                }
            }
        }
        
        if(clazz.getSuperclass() != null) {
            Class<?> superClazz = clazz.getSuperclass();
            if(superClazz != null) {
                jsonIdentityReferences.addAll(recursivelyFindJsonIdentityReference(superClazz));
                return jsonIdentityReferences;
            }
        }
            
        return jsonIdentityReferences;
    }
}
