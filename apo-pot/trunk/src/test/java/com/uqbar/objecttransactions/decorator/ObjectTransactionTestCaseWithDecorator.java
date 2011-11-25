package com.uqbar.objecttransactions.decorator;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.uqbar.objecttransactions.House;
import com.uqbar.objecttransactions.ObjectTransactionTestCase;
import com.uqbar.objecttransactions.Person;
import com.uqbar.renascent.common.transaction.TaskOwner;
import com.uqbar.renascent.framework.aop.transaction.ObjectTransactionManager;
import com.uqbar.renascent.framework.aop.transaction.utils.BasicTaskOwner;

/**
 * -Djava.system.class.loader=com.uqbar.objecttransactions.loader.ObjectTransactionClassLoader
 * 
 * @author jfernandes
 */
public class ObjectTransactionTestCaseWithDecorator extends TestCase {
	private static Log logger = LogFactory.getLog(ObjectTransactionTestCase.class);

	/**
	 * Simple test case with a single nested transaction showing
	 * the isolation between parent & child transaction.
	 * With a commit at the end of the nested transaction.
	 */
	public void testWithJustOneDoorKeeper() {
		TaskOwner testCaseOwner = new BasicTaskOwner("testCaseOwner");
		House house = new House();
		
		ObjectTransactionManager.beginNullTransaction(testCaseOwner);
		
		DoorKeeper doorKeeper = new BlockingThreadDoorKeeper(new DefaultDoorKeeper(house));
		
		this.logHouseState(house, testCaseOwner);
		doorKeeper.beginTransaction();
		this.logHouseState(house, testCaseOwner);
		
		doorKeeper.openFrontDoor();
		this.logHouseState(house, testCaseOwner);
		
		doorKeeper.openBackDoor();
		this.logHouseState(house, testCaseOwner);
		
		doorKeeper.commitTransaction();
		this.logHouseState(house, testCaseOwner);
	}
	
	/**
	 * Simple test case with a single nested transaction showing
	 * the isolation between parent & child transaction.
	 * With a commit at the end of the nested transaction.
	 */
	public void testOneDoorKeeperChangingCollectionAttribute() {
		TaskOwner testCaseOwner = new BasicTaskOwner("testCaseOwner");
		ObjectTransactionManager.begin(testCaseOwner);
		House house = new House();
		
		DoorKeeper doorKeeper = new BlockingThreadDoorKeeper(new DefaultDoorKeeper(house));
		
		doorKeeper.beginTransaction();
		doorKeeper.enterHouse(new Person("John Doe"));
		assertEquals(0, house.getPeople().size());
		
		doorKeeper.enterHouse(new Person("Foo Bar"));
		assertEquals(0, house.getPeople().size());
		
		// COMMIT & ASSERT
		doorKeeper.commitTransaction();
		assertEquals(2, house.getPeople().size());
	}
	
	protected void logHouseState(final House house, TaskOwner owner) {
		logger.debug("\t\t[ Tx: " + ObjectTransactionManager.getTransaction().getId() + "] " + "front="
			+ (house.isFrontDoorClosed() ? "CLOSED" : "OPENED") + " back="
			+ (house.isBackDoorClosed() ? "CLOSED" : "OPENED")
			+ " people=" + house.getPeople());
	}
	
}
