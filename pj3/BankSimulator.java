package pj3;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// You may add new functions or data in this class 
// You may modify any functions or data members here
// You must use Customer, Teller and ServiceArea
// to implement Bank simulator
class BankSimulator {

    // input parameters
    private int numTellers, customerQLimit;
    private int simulationTime, dataSource;
    private int chancesOfArrival, maxTransactionTime;

    // statistical data
    private int numGoaway, numServed, totalWaitingTime;

    // internal data
    private int customerIDCounter;   // customer ID counter
    private ServiceArea servicearea; // service area object
    private Scanner dataFile;	   // get customer data from file
    private Random dataRandom;	   // get customer data using random function

    // most recent customer arrival info, see getCustomerData()
    private boolean anyNewArrival;
    private int transactionTime;

    // initialize data fields
    private BankSimulator() {
        // add statements
        totalWaitingTime = 0;
        customerIDCounter = 0;
        numGoaway = 0;
        numServed = 0;
    }

    private void setupParameters() {
        // read input parameters
        // setup dataFile or dataRandom
        // add statements
        Scanner input = new Scanner(System.in);

        System.out.println("Enter simulation time (positive integer): ");
        simulationTime = input.nextInt();

        System.out.println("Enter maximum transaction time of customers");
        maxTransactionTime = input.nextInt();

        System.out.println("Enter chances (0% < & <= 100%) of new customer: ");
        chancesOfArrival = input.nextInt();

        System.out.println("Enter the number of tellers: ");
        numTellers = input.nextInt();

        System.out.println("Enter customer queue limit: ");
        customerQLimit = input.nextInt();

        dataSource = 0;
        dataRandom = new Random();
        
    }

    // Refer to step 1 in doSimulation()
    private void getCustomerData() {
        // get next customer data : from file or random number generator
        // set anyNewArrival and transactionTime
        // add statements
	anyNewArrival = ((dataRandom.nextInt(100) + 1) <= chancesOfArrival);
     transactionTime = dataRandom.nextInt(maxTransactionTime) + 1;
        
    }

    private void doSimulation() {
	// add statements

        // Initialize ServiceArea
        servicearea = new ServiceArea(numTellers, customerQLimit);

        // Time driver simulation loop
        for (int currentTime = 0; currentTime < simulationTime; currentTime++) {

            // Step 1: any new customer enters the bank?
            System.out.println("Time " + currentTime);
            getCustomerData();

            if (anyNewArrival) {
                // Step 1.1: setup customer data
                customerIDCounter++;
                Customer customer = new Customer(customerIDCounter, transactionTime, currentTime);
                System.out.println("Customer #" + customerIDCounter + " arrives with transaction time for " + transactionTime + " unites");
                // Step 1.2: check customer waiting queue too long?
                //           customer goes away or enters queue 
                //if new customer is coming, increment the id number and use as new paramter for customer object
                servicearea.insertCustomerQ(customer);
                // If the customer line is too long, a customer will be removed
                if (servicearea.isCustomerQTooLong()) {
                    servicearea.removeCustomerQ();
                    numGoaway++;
                }

            } else {
                System.out.println("\tNo new customer!");
            }
            // Step 2: free busy tellers, add to free tellerQ
            // verify if there are busy tellers. If so, check if there are any tellers who is done with the service at current time point
            if (servicearea.numBusyTellers() != 0) {
                //Check the whlole busyTellerQ by using a while loop. Keep removing the teller who finishes service with customer
                //   until encountering the peek teller who is still busy 
                while (servicearea.numBusyTellers() != 0) {
                    Teller frontBusyTeller = servicearea.getFrontBusyTellerQ();
                    if (frontBusyTeller.getEndBusyIntervalTime() == currentTime) {
                        Teller newFreeTeller = servicearea.removeBusyTellerQ();
                        newFreeTeller.busyToFree();
                        Customer finishedCustomer = newFreeTeller.getCustomer();
                        System.out.println("Customer #" + finishedCustomer.getCustomerID() + " is done.");
                        servicearea.insertFreeTellerQ(newFreeTeller);
                        System.out.println("Teller #" + newFreeTeller.getTellerID() + " is free.");
                    } else {
                        break;
                    }
                }//end of while
            }//end of if

            // Step 3: get free tellers to serve waiting customers 
            //if there is a free teller and there are customers waiting in the customer line, assign one customer to a free teller
            if (servicearea.numFreeTellers() > 0 && servicearea.numWaitingCustomers() > 0) {
                Customer servedCustomer = servicearea.removeCustomerQ();
                numServed++;
                Teller busyTeller = servicearea.removeFreeTellerQ();
                //assign one customer to a free teller
                busyTeller.freeToBusy(servedCustomer, currentTime);
                //Check if there are more than one busy teller. If so, make sure that they are in correct order
                servicearea.insertBusyTellerQ(busyTeller);
                System.out.println("Customer #" + servedCustomer.getCustomerID() + " gets a teller");
                System.out.println("Teller #" + busyTeller.getTellerID() + " starts serving customer #" + servedCustomer.getCustomerID() + " for " + servedCustomer.getTransactionTime() + " units");
            }//end of if
            System.out.println("");
        } // end simulation loop

        // clean-up// clean-up
    }

    private void printStatistics() {
        // add statements into this method!
        // print out simulation results
        // see the given example in README file 
        // you need to display all free and busy gas pumps
        System.out.println("End of Simulation Report\n");
        System.out.println("Total arrival customers: " + customerIDCounter);
        System.out.println("Customers gone-away: " + numGoaway);
        System.out.println("Customers served: " + numServed);
        System.out.println("");
        System.out.println("*** Current Teller Info ***\n");
        //print numWaitingCustomers, numBusyTellers, numFreeTellers
        servicearea.printStatistics();
        System.out.println(" ");
        
        //print out totalWaitingTime and average waiting time
        if(servicearea.numWaitingCustomers() > 0){
            int waitingCustomersCounter = 0;
            while(servicearea.numWaitingCustomers() != 0){
                Customer waitingCustomer = servicearea.removeCustomerQ();
                waitingCustomersCounter++;
                totalWaitingTime = totalWaitingTime + (simulationTime - waitingCustomer.getArrivalTime());
            }//end of while
            System.out.println("Total waiting time: " + totalWaitingTime);
            System.out.println("Average waiting time: " + (totalWaitingTime / waitingCustomersCounter));
        }
        //check if there are busy tellers. if so, print Busy Tellers info
        if (servicearea.numBusyTellers() > 0) {
            //create a while loop to remove the busy tellers from the queue and print the info one by one
            System.out.println("Busy Tellers Info :");
            while (servicearea.numBusyTellers() != 0) {
                Teller endBusyTeller = servicearea.removeBusyTellerQ();
                //cut the service off and update teller's time interval info
                endBusyTeller.setEndIntervalTime(simulationTime, 1);
                //print tellerID, totalFreeTime, totalBusyTime, totalCustomers
                endBusyTeller.printStatistics();
            }
        } else {
            System.out.println("No Busy Tellers");
        }//end of if

        //check if there are free tellers. If so, print free teller info
        if (servicearea.numFreeTellers() > 0) {
            System.out.println("Free Tellers Info :");
            while (servicearea.numFreeTellers() != 0) {
                Teller endFreeTeller = servicearea.removeFreeTellerQ();
                endFreeTeller.setEndIntervalTime(simulationTime, 0);
                endFreeTeller.printStatistics();
            }
        } else {
            System.out.println("No Free Tellers");
        }//end of if
    }//end of printStatistics()

    // *** main method to run simulation ****
    public static void main(String[] args) {
        BankSimulator runBankSimulator = new BankSimulator();
        runBankSimulator.setupParameters();
        runBankSimulator.doSimulation();
        runBankSimulator.printStatistics();
    }
}
