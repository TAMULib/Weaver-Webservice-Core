package edu.tamu.app.model.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.app.model.repo.UserRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class UserTest {
	
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
		Assert.assertEquals("Test User1 was not added.", testUser1.getUIN(), assertUser.getUIN());
	
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
