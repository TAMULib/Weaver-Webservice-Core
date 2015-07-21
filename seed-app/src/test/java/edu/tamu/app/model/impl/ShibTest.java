package edu.tamu.app.model.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.framework.model.Credentials;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class ShibTest {

	private Map<String, String> aggieJackToken;
	
	private long timestamp = new Date().getTime()+(5*60*1000);
	
	@Before
	public void setup() {
		
		aggieJackToken = new HashMap<>();
		aggieJackToken.put("lastName","Daniels");
		aggieJackToken.put("firstName","Jack");
		aggieJackToken.put("netid","aggiejack");
		aggieJackToken.put("uin","123456789");
		aggieJackToken.put("exp",String.valueOf(timestamp));
		aggieJackToken.put("email","aggiejack@tamu.edu");
		
	}
	
	@Test
	public void testCreateShib() {
		
		Credentials shib = new Credentials(aggieJackToken);
		
		Assert.assertEquals("Last name did not match.", "Daniels", shib.getLastName());
		Assert.assertEquals("First name did not match.", "Jack", shib.getFirstName());
		Assert.assertEquals("Netid did not match.", "aggiejack", shib.getNetid());
		Assert.assertEquals("UIN did not match.", "123456789", shib.getUin());
		Assert.assertEquals("Expiration did not match.", String.valueOf(timestamp), shib.getExp());
		Assert.assertEquals("Email did not match.", "aggiejack@tamu.edu", shib.getEmail());
			
	}
	
	private Map<String, String> createToken(String uin, String firstName, String lastName, String email, String netid, Long time) {
		
		Map<String, String> newToken = new HashMap<>();
		
		newToken.put("uin",uin);
		newToken.put("firstName",firstName);
		newToken.put("lastName",lastName);
		newToken.put("email",email);
		newToken.put("netid",netid);
		if(time!=null)
			newToken.put("exp",String.valueOf(time));
		else
			newToken.put("exp", String.valueOf(timestamp));
		
		return newToken;
	}
	
	@SuppressWarnings("unused")
	private Map<String, String> createToken(String uin, String firstName, String lastName, String email, String netid) {
		return createToken(uin, firstName, lastName, email, netid, null);
	}
}
