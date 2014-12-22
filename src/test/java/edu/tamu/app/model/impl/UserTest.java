package edu.tamu.app.model.impl;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import edu.tamu.app.repo.UserRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AnnotationConfigContextLoader.class})
public class UserTest {
	
	@Configuration
    static class ContextConfiguration {
		
		@Bean
		public DataSource dataSource() {
			return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)
				.build();
		}
		
        // this bean will be injected into the OrderServiceTest class
        @Bean
//        public UserRepo userRepo() {
//        	UserRepo userRepo = new UserRepo();
//            // set properties, etc.
//            return userRepo;
//        }
    }
	
	@Autowired
	private UserRepo userRepo;
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testMethod() {
		
		UserImpl testUser1 = new UserImpl();
		testUser1.setFirstName("Test");
		testUser1.setLastName("User1");
		testUser1.setEmail("test1@email.com");
		testUser1.setUIN(Long.parseLong("123456789"));
		
		UserImpl testUser2 = new UserImpl();
		testUser2.setFirstName("Test");
		testUser2.setLastName("User2");
		testUser2.setEmail("test2@email.com");
		testUser2.setUIN(Long.parseLong("123456789"));
		
		userRepo.save(testUser1);		
		UserImpl assertUser = userRepo.getUserByUin(Long.parseLong("123456789"));		
		Assert.assertEquals("Test User1 was not added.", testUser1, assertUser);
	
		userRepo.save(testUser2);		
		List<UserImpl> allUsers = (List<UserImpl>) userRepo.findAll();		
		Assert.assertEquals("Duplicate UIN found.", 1, allUsers.size());
		
		testUser1.setFirstName("Change");
		userRepo.save(testUser1);
		testUser1 = userRepo.getUserByUin(Long.parseLong("123456789"));
		Assert.assertEquals("Update fail.", "Change", testUser1.getFirstName());
		
		userRepo.delete(testUser1);		
		allUsers = (List<UserImpl>) userRepo.findAll();		
		Assert.assertEquals("Test User1 was not removed.", 0, allUsers.size());
		
	}
}
