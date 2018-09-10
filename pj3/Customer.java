// DO NOT ADD NEW METHODS OR DATA FIELDS!

package pj3;

class Customer
{
    private int customerID;
    private int transactionTime;
    private int arrivalTime;

    // default constructor
    Customer()
    {
	// add statements
        this(0,0,0);
    }

    // constructor to set customerID, transactionTime and arrivalTime
    Customer(int customerid, int duration, int arrivaltime)
    {
	// add statements
        customerID = customerid;
        transactionTime = duration;
        arrivalTime = arrivaltime;
    }

    int getTransactionTime() 
    {
	// add statements
  	return transactionTime; 
    }

    int getArrivalTime() 
    {
	// add statements
  	return arrivalTime; 
    }

    int getCustomerID() 
    {
	// add statements
  	return customerID; 
    }

    public String toString()
    {
        return "customerID="+customerID+":transactionTime="+
               transactionTime+":arrivalTime="+arrivalTime;
    }

    public static void main(String[] args) {
        // quick check!
	Customer mycustomer = new Customer(1,15,18);
	System.out.println("Customer Info:"+mycustomer);

    }
}
