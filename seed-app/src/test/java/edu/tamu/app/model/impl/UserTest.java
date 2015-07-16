package edu.tamu.app.model.impl;

import static org.junit.Assert.*;

import org.junit.After;
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
import edu.tamu.app.model.AppUser;
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
	public void testCreateAndDelete() {
		
		userRepo.create(123456789l);		
		AppUser testUser1 = userRepo.getUserByUin(123456789l);				
		assertTrue("Test User1 was not added.", testUser1.getUin().equals(123456789l));
		
		userRepo.delete(testUser1);				
		assertEquals("Test User1 was not removed.", 0, userRepo.count());
	}
	
	@Test
	public void testDuplicateUser() {
		
		userRepo.create(123456789l);		
		AppUser testUser1 = userRepo.getUserByUin(123456789l);				
		assertTrue("Test User1 was not added.", testUser1.getUin().equals(123456789l));
		
		userRepo.create(123456789l);
				
		assertEquals("Duplicate UIN found.", 1, userRepo.count());
	}
			
	@After
	public void cleanUp() {
		for(AppUser user : userRepo.findAll())
			userRepo.delete(user);
	}
}
