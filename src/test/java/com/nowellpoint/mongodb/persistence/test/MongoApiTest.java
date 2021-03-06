package com.nowellpoint.mongodb.persistence.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.mongodb.persistence.DocumentManager;
import com.nowellpoint.mongodb.persistence.DocumentManagerFactory;
import com.nowellpoint.mongodb.persistence.datastore.Datastore;
import com.nowellpoint.mongodb.persistence.exception.DatastoreConfigurationException;
import com.nowellpoint.mongodb.persistence.test.model.Identity;
import com.nowellpoint.mongodb.persistence.test.model.NameValuePair;
import com.nowellpoint.mongodb.persistence.test.model.Session;
import com.nowellpoint.mongodb.persistence.test.model.Token;

public class MongoApiTest {
	
	private static DocumentManagerFactory documentManagerFactory;
	private static DocumentManager documentManager;
	
	@BeforeClass
	public static void initDB() {			
		try {			
			documentManagerFactory = Datastore.createDocumentManagerFactory();
			documentManager = documentManagerFactory.createDocumentManager();
		} catch (DatastoreConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void before() {
		Assume.assumeTrue(documentManagerFactory.isOpen());
	}
	
	@After
	public void after() {
		Assume.assumeTrue(documentManagerFactory.isOpen());
	}
	
	@Test
	public void testQueryAllProperties() {
		Assume.assumeTrue(documentManagerFactory.isOpen());
		
		List<NameValuePair> properties = documentManager.createQuery(NameValuePair.class).getResultList();
		
		assertNotNull(properties);
		assertNotEquals(properties.size(), 0);

	}
	
	@Test
	public void testCreateSession() {
		Assume.assumeTrue(documentManagerFactory.isOpen());
		
		Token token = new Token();
		token.setAccessToken("00D300000000lnE!AR4AQIsdrRtBjyLpPLuX4z.DIMX8l1.bSqiDOm5O08Cc20wWX8slYd5oxqRaWECBu5KkKZ9rAdQz4mmfTvhvWecTuBDXWNi1");
		token.setId("https://login.salesforce.com/id/00D300000000lnEEAQ/00530000000fo9KAAQ");
		token.setInstanceUrl("https://na1.salesforce.com");
		token.setIssuedAt("1385144409093");
		token.setSignature("dIDQKrslQuYSzD6+sXLcshPuf6uwyxxpt+Px8v9HLLA=");
		
		Identity identity = new Identity();
		identity.setDisplayName("John Herson");
		identity.setLocale(Locale.getDefault());
		
		Session session = new Session();
		session.setSessionId("wVSFPEk2MEb1oKBZyg+dy7WQ");
		session.setToken(token);
		session.setIdentity(identity);
		
		documentManager.persist(session);
		
		assertNotNull(session.getId());
		
		System.out.println(session.getId());
		
		session = documentManager.find(Session.class, session.getId());
		
		assertNotNull(session);
		assertNotNull(session.getToken());
		assertNotNull(session.getIdentity().getLocale());
		assertNull(session.getIdentity().getEmail());
		
		documentManager.refresh(session);
		
		assertNotNull(session);
		assertNotNull(session.getToken());
		assertNotNull(session.getIdentity().getLocale());
		
		session.getIdentity().setEmail("john.d.herson@gmail.com");
		
		session = documentManager.merge(session);
		
		session = documentManager.find(Session.class, session.getId());
		
		assertNotNull(session);
		assertNotNull(session.getIdentity().getEmail());
		
		documentManager.remove(session);
		
		session = documentManager.find(Session.class, session.getId());
		
		assertNull(session);
	}
	
	@AfterClass
	public static void closeDB() {		
		Assume.assumeTrue(documentManagerFactory.isOpen());
		documentManagerFactory.close();
	}
}