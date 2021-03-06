package edu.sqa.cars.domain;

import edu.sqa.cars.main.CarSalesSystem;

import java.io.*;
import java.util.Vector;
/**
 * Stores manufacturers objects, and performs searches
 * @
 *
 * PUBLIC FEATURES:
 * // Constructors
 *    public CarsCollection()
 *    public CarsCollection(Manufacturer man)
 *
 * // Methods
 *    public int addCar(Car c)
 *    public int carsCount()
 *    public int manufacturerCount()
 *    public Car[] getAllCars()
 *    public Manufacturer[] getAllManufacturers()
 *    public double getAverageAge()
 *    public double getAverageDistance()
 *    public double getAveragePrice()
 *    public void loadCars(String file) throws IOException, ClassNotFoundException
 *    public void saveCars(String file) throws IOException
 *    public Car[] search(int minPrice, int maxPrice, double minDistance, double maxDistance)
 *    public Car[] search(int minAge, int maxAge)
 *
 * COLLABORATORS:
 *    Manufacturer
 *
 * @version 1.0, 16 Oct 2004
 * @author Adam Black
 */
public class CarsCollection
{
	/**
	 * this constant is returned by the addCar method to indicate the car was successfully
	 * added to the Car Sales System
	 */
	public static final int NO_ERROR = 0;
	/**
	 * this constant is returned by the addCar method to indicate the car wasn't successfully
	 * added to the Car Sales System because the manufacturer has reached it's maximum of
	 * 20 cars
	 */
	public static final int CARS_MAXIMUM_REACHED = 1;
	/**
	 * this constant is returned by the addCar method to indicate the car wasn't successfully
	 * added to the Car Sales System because the system has reached it's maximum of
	 * 20 manufacturers
	 */
	public static final int MANUFACTURERS_MAXIMUM_REACHED = 2;

	private static final int MAX_MANUFACTURES = 20;
	private static final int MAX_CARS = 20;

	private Manufacturer[] manufacturer = new Manufacturer[0];

	/**
	 * adds a car to a CarCollection and files it in an appropriate manufacturer, or creates a new
	 * manufacturer if none exist for the car
	 *
	 * @param c car to add to collection
	 * @return either one of NO_ERROR, CARS_MAXIMUM_REACHED, or MANUFACTURERS_MAXIMUM_REACHED
	 */
	public int addCar(Car c)
	{
		Manufacturer man;
		String name = c.getManufacturer();
		int index = -1;
		int result = NO_ERROR;

		for (int i = 0; i < manufacturer.length; i++)
		{
			// if manufacturer already exists
			if (manufacturer[i].getManufacturerName().equalsIgnoreCase(name))
				index = i;
		}

		// if manufacturer doesn't exist
		if (index == -1)
		{
			if (manufacturer.length < MAX_MANUFACTURES)
			{
				man = new Manufacturer(name, c);
				addManufacturer(man);
			}
			else
				result = MANUFACTURERS_MAXIMUM_REACHED;
		}
		else
		{
			if (manufacturer[index].carCount() < MAX_CARS)
				manufacturer[index].addCar(c);
			else
				result = CARS_MAXIMUM_REACHED;
		}

		return result;
	}

	/**
	 * add a Manufacturer object to the CarsCollection
	 *
	 * @param man Manufacturer object to add
	 */
	private void addManufacturer(Manufacturer man)
	{
		manufacturer = resizeArray(manufacturer, 1);
		manufacturer[manufacturer.length - 1] = man;
	}

	/**
	 * get the entire count of cars in the CarsCollection from all manufacturers
	 *
	 * @return integer representing total number of cars
	 */
	public int carsCount()
	{
		int count = 0;

		for (Manufacturer value : manufacturer) count += value.carCount();

		return count;
	}

	/**
	 * get number of manufacturers in CarsCollection
	 *
	 * @return number of manufacturers
	 */
	public int manufacturerCount()
	{
		return manufacturer.length;
	}

	/**
	 * get all cars in the CarsCollection from all manufacturers
	 *
	 * @return entire collection of cars in CarsCollection from all manufacturers
	 */
	public Car[] getAllCars()
	{
		Vector result = new Vector();
		Car[] car;
		for (Manufacturer value : manufacturer) {
			car = value.getAllCars();
			for (Car item : car) {
				result.addElement(item);
			}
		}

		return CarSalesSystem.vectorToCar(result);
	}

	/**
	 * calculate the average age from the entire collection of cars
	 *
	 * @return value indicating the average age of all the cars in the collection
	 */
	public double getAverageAge()
	{
		Car[] car;
		double result = 0;
		int count = 0;

		for (Manufacturer value : manufacturer) {
			car = value.getAllCars();
			for (Car item : car) {
				result += item.getAge();
				count++;
			}
		}
		if (count == 0)
			return 0;
		else
			return (result / count);
	}

	/**
	 * calculate the average distance travelled from the entire collection of cars
	 *
	 * @return value indicating the average distance travelled of all the cars in the collection
	 */
	public double getAverageDistance()
	{
		Car[] car;
		double result = 0;
		int count = 0;

		for (Manufacturer value : manufacturer) {
			car = value.getAllCars();
			for (Car item : car) {
				result += item.getKilometers();
				count++;
			}
		}
		if (count == 0)
			return 0;
		else
			return (result / count);
	}

	/**
	 * calculate the average price from the entire collection of cars
	 *
	 * @return value indicating the average price of all the cars in the collection
	 */
	public double getAveragePrice()
	{
		Car[] car;
		double result = 0;
		int count = 0;

		for (Manufacturer value : manufacturer) {
			car = value.getAllCars();
			for (Car item : car) {
				result += item.getPrice();
				count++;
			}
		}
		if (count == 0)
			return 0;
		else
			return (result / count);
	}

	/**
	 * load entire collectoin of cars into the manufacturer object from a data file
	 *
	 * @param file filename of binary file to load car data from
	 */
	public void loadCars(String file) throws IOException, ClassNotFoundException
	{
		try (ObjectInputStream inp = new ObjectInputStream(new FileInputStream(file))) {
			manufacturer = (Manufacturer[]) inp.readObject();
		}
	}

	/**
	 * resize the manufacturer array while maintaining data integrity
	 *
	 * @param inArray array to resize
	 * @param extendBy indicates how many elements should the array be extended by
	 * @return the resized Manufacturer array
	 */
	private Manufacturer[] resizeArray(Manufacturer[] inArray, int extendBy)
	{
		Manufacturer[] result = new Manufacturer[inArray.length + extendBy];

		System.arraycopy(inArray, 0, result, 0, inArray.length);

		return result;
	}

	/**
	 * Save all cars to a binary file
	 *
	 * @param file of the binary file
	 */
	public void saveCars(String file) throws IOException
	{
		int flag;
		int items = manufacturer.length;
		Manufacturer temp;

		if (manufacturer.length > 0)
		{
			do
			{
				flag = 0;
				for (int i = 0; i < items; i++)
				{
					if (i+1 < items && manufacturer[i].getManufacturerName().compareTo(manufacturer[i + 1].getManufacturerName()) > 0)
					{
						temp = manufacturer[i];
						manufacturer[i] = manufacturer[i + 1];
						manufacturer[i + 1] = temp;
						flag++;
					}
				}
			}
			while (flag > 0);

			try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
				out.writeObject(manufacturer);
			}
		}
	}

	/**
	 * search by price and distance travelled
	 *
	 * @param minPrice minimum price of car
	 * @param maxPrice maximum price of car
	 * @param minDistance minimum distance travelled by car
	 * @param maxDistance maximum distance travelled by car
	 * @return array of Car objects that matched the search criteria
	 */
	public Car[] search(int minPrice, int maxPrice, double minDistance, double maxDistance)
	{
		Vector result = new Vector();
		int price;
		double distance;
		Car[] car;
		car = getAllCars();

		for (Car value : car) {
			price = value.getPrice();
			distance = value.getKilometers();

			if (price >= minPrice && price <= maxPrice && distance >= minDistance && distance <= maxDistance)
				result.add(value);
		}

		return CarSalesSystem.vectorToCar(result);
	}

	/**
	 * search by age
	 *
	 * @param minAge minimum age of car
	 * @param maxAge maximum age of car
	 * @return array of Car objects that matched the search criteria
	 */
	public Car[] search(int minAge, int maxAge)
	{
		Car[] car;
		car = getAllCars();
		Vector result = new Vector();

		/* Putting the if statement first will increase effeciency since it isn't constantly
		checking the condition for each loop. It does use almost double the amount of code though */
		if (maxAge == -1)
		{
			for (Car value : car) {
				if (value.getAge() >= minAge) {
					result.addElement(value);
				}
			}
		}
		else
		{
			for (Car value : car) {
				if (value.getAge() >= minAge && value.getAge() <= maxAge) {
					result.addElement(value);
				}
			}
		}

		return CarSalesSystem.vectorToCar(result);
	}
}