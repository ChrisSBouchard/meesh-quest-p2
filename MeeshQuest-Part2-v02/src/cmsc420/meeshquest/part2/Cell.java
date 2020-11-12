package cmsc420.meeshquest.part2;

import java.util.ArrayList;

public class Cell {
	
	public int maxx, minx, maxy, miny;
	
	public Cell(ArrayList<City> list) {
		maxx = maxy = -1;
		minx = miny = Integer.MAX_VALUE;
		for(City c : list) {
			if(c.getX() < minx) {
				minx = c.getX();
			}
			if(c.getX() > maxx) {
				maxx = c.getX();
			}
			if(c.getY() < miny) {
				miny = c.getY();
			}
			if(c.getY() > maxy) {
				maxy = c.getY();
			}
		}
	}
	
	public Cell(int maxx, int minx, int maxy, int miny) {
		this.maxx = maxx;
		this.minx = minx;
		this.maxy = maxy;
		this.miny = miny;
	}
	
	public Cell leftPart(int cd, City city) {
		if(cd == 0) {
			return new Cell(city.getX(), minx, maxy, miny);
		} else {
			return new Cell(maxx, minx, city.getY(), miny);
		}
	}
	
	public Cell rightPart(int cd, City city) {
		if(cd == 0) {
			return new Cell(maxx, city.getX(), maxy, miny);
		} else {
			return new Cell(maxx, minx, maxy, city.getY());
		}
	}
	
	public double distanceTo(City city) {
		if(city.getX() < maxx && city.getX() > minx && city.getY() < maxy && city.getY() > miny) {
			return 0;
		} else if(city.getX() < minx && city.getY() < maxy && city.getY() > miny) {
			return (miny - city.getX());
		} else if(city.getX() > maxx && city.getY() < maxy && city.getY() > miny) {
			return (city.getX() - maxx);
		} else if(city.getX() > minx && city.getX() < maxx && city.getY() < miny) {
			return (miny - city.getY());
		} else if(city.getX() > minx && city.getX() < maxx && city.getY() > maxy) {
			return (city.getY() - maxy);
		} else {
			double min1 = Math.min(dist(city, maxx, maxy), dist(city, maxx, miny));
			double min2 = Math.min(dist(city, minx, maxy), dist(city, minx, miny));
			return Math.min(min1, min2);
		}
		
	}
	
	private double dist(City city, int x, int y) {
		return Math.sqrt(  ((city.getX() - x)*(city.getX() - x)) + ((city.getY() - y)*(city.getY() - y))          );
	}
	
}
