package com.mwb.framework.util;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class GeoLocationUtility {
	
	public static final double PI = 3.14159265359; 
	public static final double TWOPI = 6.28318530718; 
	public static final double DE2RA = 0.01745329252; 
	public static final double RA2DE = 57.2957795129; 
	public static final double ERAD = 6378.135; 
	public static final double ERADM = 6378135.0; 
	public static final double AVG_ERAD = 6371.0; 
	public static final double FLATTENING = 1.0/298.26;// Earth flattening (WGS '72) 
	public static final double EPS = 0.000000000005; 
	public static final double KM2MI = 0.621371; 
	
    public static double getDistance(double point1Lat, double point1Lng, double point2Lat, double point2Lng) {
        LatLng point1 = new LatLng(point1Lat, point1Lng);
        LatLng point2 = new LatLng(point2Lat, point2Lng);

        return LatLngTool.distance(point1, point2, LengthUnit.KILOMETER);
    }

    public static double getDistanceMeters(double point1Lat, double point1Lng, double point2Lat, double point2Lng) {
        LatLng point1 = new LatLng(point1Lat, point1Lng);
        LatLng point2 = new LatLng(point2Lat, point2Lng);

        return LatLngTool.distance(point1, point2, LengthUnit.METER);
    }
    
    public static double getDistanceMeters(BigDecimal point1Lat, BigDecimal point1Lng, BigDecimal point2Lat,
            BigDecimal point2Lng) {
    	if (point1Lat == null || point1Lng == null || point2Lat == null || point2Lng == null) {
    		return Double.MAX_VALUE;
    	}
        return GeoLocationUtility.getDistanceMeters(point1Lat.doubleValue(), point1Lng.doubleValue(), point2Lat.doubleValue(),
                point2Lng.doubleValue());
    }
    
    public static double getDistance(BigDecimal point1Lat, BigDecimal point1Lng, BigDecimal point2Lat,
            BigDecimal point2Lng) {
    	if (point1Lat == null || point1Lng == null || point2Lat == null || point2Lng == null) {
    		return Double.MAX_VALUE;
    	}        
        return GeoLocationUtility.getDistance(point1Lat.doubleValue(), point1Lng.doubleValue(), point2Lat.doubleValue(),
                point2Lng.doubleValue());
    }
    
    /**
     * @Description: 
     * 	Lon = X+[r*sin(a*pi/180)]/[111*cos(Y*pi/180)];
     *  Lat = Y+[r*cos(a*pi/180)]/111;
     * @param point
     * @param azimuth
     * @return
     */
    public static Point getPointByAzimuth(Point point, double distanceInKM, double azimuth) {
    	
    	double longitude = point.getLongitude().doubleValue() + 
    			(distanceInKM * Math.sin(azimuth * Math.PI / 180)) / (111 * Math.cos(point.getLatitude().doubleValue() * Math.PI / 180));
    	double latitude = point.getLatitude().doubleValue() + 
    			distanceInKM * Math.cos(azimuth * Math.PI / 180) / 111; 
    	
    	return Point.getPoint(new BigDecimal(longitude), new BigDecimal(latitude));
    }
    
    /**
     * @Description: 劣角，两个方向小于180度的角，返回-1表示方向未知
     * @param direction1
     * @param direction2
     * @return
     */
    public static double getInferiorAngle(Direction direction1, Direction direction2) {
    	double result = getRoundAngle(direction1, direction2);
    	if (result < 0) {
    		return result;
    	}
    	if (result > 180) {
    		result = 360 - result;
    	}
    	return result;
    }
    
    /**
     * @Description: 两个方向的周角，即：两个方向的顺时针夹角，取值0-360，返回-1表示方向未知
     * @param direction1
     * @param direction2
     * @return
     */
    public static double getRoundAngle(Direction direction1, Direction direction2) {
    	double azimuth1 = getAzimuth(direction1.getStartPoint(), direction1.getEndPoint());
    	if (azimuth1 < 0) {
    		return azimuth1;
    	}
    	double azimuth2 = getAzimuth(direction2.getStartPoint(), direction2.getEndPoint());
    	if (azimuth2 < 0) {
    		return azimuth2;
    	}
    	if (azimuth1 > azimuth2) {
    		return azimuth1 - azimuth2;
    	}
    	return azimuth2 - azimuth1;
    }
    
    public static double getAzimuth(Point point1, Point point2) {
    	return getAzimuth(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(), point2.getLongitude());
    }
    
    public static double getAzimuth(BigDecimal point1Lat, BigDecimal point1Lng, BigDecimal point2Lat, BigDecimal point2Lng) {
    	if (point1Lat == null || point1Lng == null || point2Lat == null || point2Lng == null) {
    		return -1;
    	} 
    	return getAzimuth(point1Lat.doubleValue(), point1Lng.doubleValue(), point2Lat.doubleValue(), point2Lng.doubleValue());
    }
    
    /**
     * @Description: 计算方位角
     * @param point1Lat
     * @param point1Lng
     * @param point2Lat
     * @param point2Lng
     * @return
     */
    public static double getAzimuth(double point1Lat, double point1Lng, double point2Lat, double point2Lng) {
    	double result = 0.0;   
       
        int ipoint1Lat = (int)(0.50 + point1Lat * 360000.0);   
        int ipoint2Lat = (int)(0.50 + point2Lat * 360000.0);   
        int ipoint1Lng = (int)(0.50 + point1Lng * 360000.0);   
        int ipoint2Lng = (int)(0.50 + point2Lng * 360000.0);   
       
        point1Lat *= DE2RA;   
        point1Lng *= DE2RA;   
        point2Lat *= DE2RA;   
        point2Lng *= DE2RA;   
       
        if ((ipoint1Lat == ipoint2Lat) && (ipoint1Lng == ipoint2Lng)) {
        	return result;
        } else if (ipoint1Lat == ipoint2Lat) {
        	if (ipoint1Lng > ipoint2Lng) {   
                result = 90.0;
        	} else {   
                result = 270.0;
        	}
        } else if (ipoint1Lng == ipoint2Lng) {   
            if (ipoint1Lat > ipoint2Lat) {
                result = 180.0; 
            }
        } else {   
            double c = Math.acos(Math.sin(point2Lat)*Math.sin(point1Lat) + Math.cos(point2Lat)*Math.cos(point1Lat)*Math.cos((point2Lng-point1Lng)));  
            double A = Math.asin(Math.cos(point2Lat)*Math.sin((point2Lng-point1Lng))/Math.sin(c));
            result = (A * RA2DE);   
       
            if ((ipoint2Lat > ipoint1Lat) && (ipoint2Lng > ipoint1Lng)) {   
            
            } else if ((ipoint2Lat < ipoint1Lat) && (ipoint2Lng < ipoint1Lng)) {   
                result = 180.0 - result; 
            } else if ((ipoint2Lat < ipoint1Lat) && (ipoint2Lng > ipoint1Lng)) {   
                result = 180.0 - result;
            } else if ((ipoint2Lat > ipoint1Lat) && (ipoint2Lng < ipoint1Lng)) {   
                result += 360.0;   
            }
        }
       
        return result;   
    }   
    
	public static Point marsToBaidu(Point point) {
		if (point.getLatitude() == null || point.getLongitude() == null) {
			return point;
		}
		double xPi = 3.14159265358979324 * 3000.0 / 180.0;
		double x = point.getLongitude().doubleValue();
		double y = point.getLatitude().doubleValue();
		double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * xPi);
		double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * xPi);
		x = z * Math.cos(theta) + 0.0065;
		y = z * Math.sin(theta) + 0.006;
		BigDecimal longitude = BigDecimal.valueOf(x);
		BigDecimal latitude = BigDecimal.valueOf(y);
		return Point.getPoint(longitude, latitude);
	}

	public static Point baiduToMars(Point point) {
		if (point.getLatitude() == null || point.getLongitude() == null) {
			return point;
		}
		double x = point.getLongitude().doubleValue() - 0.0065;
		double y = point.getLatitude().doubleValue() - 0.006;
		double xPi = x / 180.0;
		double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * xPi);
		double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * xPi);
		BigDecimal longitude = BigDecimal.valueOf(z * Math.cos(theta));
		BigDecimal latitude = BigDecimal.valueOf(z * Math.sin(theta));
		return Point.getPoint(longitude, latitude);
	}
	
	// 判断一个坐标点是否在一个多边型内部
    public static boolean isPointInPolygon(List<Point> polygon, Point point) {
    	int nCross = 0;
    	int nCount = polygon.size();
    	
    	for (int i = 0; i < nCount; i++) {
    		Point firstPoint = polygon.get(i);
    		Point secondPoint = polygon.get((i + 1) % nCount);
    		if (firstPoint.getLatitude().equals(secondPoint.getLatitude())) {
    			if (point.getLatitude().equals(firstPoint.getLatitude()) 
    					&& point.getLongitude().doubleValue() >= Math.min(firstPoint.getLongitude().doubleValue(), secondPoint.getLongitude().doubleValue()) 
    					&& point.getLongitude().doubleValue() <= Math.max(firstPoint.getLongitude().doubleValue(), secondPoint.getLongitude().doubleValue())) {
    				return true;
    			}
    			
    			continue;
    		}
    		
    		if (point.getLatitude().doubleValue() <= Math.min(firstPoint.getLatitude().doubleValue(), secondPoint.getLatitude().doubleValue()) 
    				|| point.getLatitude().doubleValue() > Math.max(firstPoint.getLatitude().doubleValue(), secondPoint.getLatitude().doubleValue())) {
    			continue;
    		}
    		
    		double longitude = (point.getLatitude().doubleValue() - firstPoint.getLatitude().doubleValue()) 
    				* (secondPoint.getLongitude().doubleValue() - firstPoint.getLongitude().doubleValue())
    				/ (secondPoint.getLatitude().doubleValue() - firstPoint.getLatitude().doubleValue()) 
    				+ firstPoint.getLongitude().doubleValue();
    		
    		if (longitude > point.getLongitude().doubleValue()) {
    			nCross++; 
    		} else if (longitude == point.getLongitude().doubleValue()) {
    			return true;
    		}
    	}
    	
    	if (nCross % 2 == 1) {
    		return true;
    	}
    	
    	return false;
    }
    
	public static void main(String[] args) {
		Point p1 = new Point(new BigDecimal("116.33284764375"), new BigDecimal("40.08292663317"));
		Point p2 = new Point(new BigDecimal("116.36122062328"), new BigDecimal("40.089639908719"));
		Point p3 = new Point(new BigDecimal("116.411024"), new BigDecimal("39.975064"));
		Point p4 = new Point(new BigDecimal("116.363162"), new BigDecimal("39.989659"));
		
		System.out.println(getPointByAzimuth(p1, 0.5, 270));
		
		String testPoints = "116.33284764375,40.08292663317;116.33247610459,40.082821193845;116.33253620123,40.082680492813;116.33324927614,40.082891854772;116.33382167641,40.083065332556;116.33491617184,40.083423534478;116.33532777543,40.083559334672;116.33547833143,40.083607844532;116.33558873318,40.083636757221;116.33572922816,40.083685405034;116.33796681736,40.084385722646;116.33851846677,40.084571548142;116.33938084006,40.084845489734;116.33982190806,40.084952650966;116.34004244207,40.085011303211;116.34013263194,40.085030761944;116.34023288283,40.085050151669;116.34032307271,40.085059605021;116.34048342024,40.085078649725;116.34097443403,40.08513592181;116.34259713311,40.085298560468;116.34322792324,40.085366389711;116.34424902711,40.085473619119;116.34431909494,40.085483486424;116.34527974285,40.085591819679;116.34548985651,40.085601548962;116.34565990574,40.085621352536;116.34687019276,40.085751007146;116.34791995258,40.085801792492;116.34809988317,40.085801999497;116.34866958852,40.085822976042;116.35077755223,40.085909228213;116.35097724555,40.085920061478;116.35107713712,40.085930480732;116.35126694907,40.085931446755;116.35223532241,40.085976159823;116.35291399223,40.085999965379;116.3529439058,40.086000172384;116.35399159952,40.086056960677;116.35487939485,40.086093462483;116.35496913557,40.086094152498;116.3556472664,40.086129619262;116.35594640214,40.086142177528;116.35609597,40.086153493766;116.35626548025,40.086165017006;116.35634524978,40.08616570702;116.35645493289,40.08616667304;116.35643526,40.086326549165;116.35642546847,40.086396516413;116.35636635996,40.08722611269;116.3563467769,40.087536061541;116.35629754976,40.088095787474;116.35627787687,40.088275668824;116.35624832262,40.088555529306;116.35619900565,40.089025272793;116.35617942259,40.089265180184;116.35616963106,40.089345149125;116.35625883279,40.089345839107;116.35744504522,40.089396966741;116.35887990861,40.089461962948;116.35957726318,40.089489838159;116.36045373986,40.089520266286;116.3608022824,40.089534548871;116.36237530518,40.089604995781;116.36237539501,40.089695038003;116.36212647455,40.089681652424;116.36122062328,40.089639908719";
		
		long start = System.currentTimeMillis();
		String[] latlngs = testPoints.split(";");
		String[] latlng = null;
		int count = 0;
		for (int i=0;i<3;i++) {
			for (String lls : latlngs) {
				latlng = lls.split(",");
				getDistanceMeters(p1.getLatitude().doubleValue(), p1.getLongitude().doubleValue(), Double.parseDouble(latlng[1]), Double.parseDouble(latlng[0]));
				count++;
			}
		}
		System.out.println((System.currentTimeMillis() - start)+"," + count);
		
		start = System.currentTimeMillis();
		for (int i=0; i<1000000; i++) {
			getDistanceMeters(p1.getLatitude().doubleValue(), p1.getLongitude().doubleValue(), p2.getLatitude().doubleValue(), p2.getLongitude().doubleValue());
		}
		System.out.println(System.currentTimeMillis() - start);
		
		Direction d1 = new Direction(p1, p2);
		Direction d2 = new Direction(p3, p4);
		
		System.out.println(getDistanceMeters(p1.getLatitude().doubleValue(), p1.getLongitude().doubleValue(), p2.getLatitude().doubleValue(), p2.getLongitude().doubleValue()));
		System.out.println(getDistanceMeters(p3.getLatitude().doubleValue(), p3.getLongitude().doubleValue(), p4.getLatitude().doubleValue(), p4.getLongitude().doubleValue()));
		
		System.out.println(getRoundAngle(d1, d2));
		System.out.println(getInferiorAngle(d1, d2));
	}
	
    public static class Point implements Serializable {
    	private static final long serialVersionUID = 1L;
		
    	private BigDecimal longitude;//经度
    	private BigDecimal latitude;//纬度
    	
    	public static Point getPoint(BigDecimal longitude, BigDecimal latitude) {
    		return new Point(longitude, latitude);
    	}
    	
    	private Point(BigDecimal longitude, BigDecimal latitude) {
    		this.longitude = longitude;
    		this.latitude = latitude;
    	}

    	public BigDecimal getLongitude() {
    		return longitude;
    	}

    	public BigDecimal getLatitude() {
    		return latitude;
    	}

		@Override
		public String toString() {
			return "Point [longitude=" + longitude + ", latitude=" + latitude
					+ "]";
		}    	
    }
    
    public static class Direction implements Serializable {
		private static final long serialVersionUID = 1L;

		private Point startPoint;
    	private Point endPoint;

    	public Direction(Point startPoint, Point endPoint) {
			this.startPoint = startPoint;
			this.endPoint = endPoint;
		}
		public Point getStartPoint() {
			return startPoint;
		}
		public void setStartPoint(Point startPoint) {
			this.startPoint = startPoint;
		}
		public Point getEndPoint() {
			return endPoint;
		}
		public void setEndPoint(Point endPoint) {
			this.endPoint = endPoint;
		}
		@Override
		public String toString() {
			return "Direction [startPoint=" + startPoint + ", endPoint="
					+ endPoint + "]";
		}
		
    }
    
    public static class EvilTransform {//World Geodetic System to Mars Geodetic System
		// Krasovsky 1940
		static double pi = 3.14159265358979324;
		static double a = 6378245.0;
		static double ee = 0.00669342162296594323;

		public static Point transform(Point point) {
			double worldLongitude = point.getLongitude().doubleValue();
			double worldLatitude = point.getLatitude().doubleValue();

			double dLat = transformLatitude(worldLongitude - 105.0, worldLatitude - 35.0);
			double dLon = transformLongitude(worldLongitude - 105.0, worldLatitude - 35.0);
			double radLat = worldLatitude / 180.0 * pi;
			double magic = Math.sin(radLat);
			magic = 1 - ee * magic * magic;
			double sqrtMagic = Math.sqrt(magic);
			dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
			dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
			double marsLatitude = worldLatitude + dLat;
			double marsLongitude = worldLongitude + dLon;
			return new Point(BigDecimal.valueOf(marsLongitude), BigDecimal.valueOf(marsLatitude));
		}

		private static double transformLatitude(double x, double y) {
			double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
			ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x	* pi)) * 2.0 / 3.0;
			ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
			ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi	/ 30.0)) * 2.0 / 3.0;
			return ret;
		}

		private static double transformLongitude(double x, double y) {
			double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
			ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x	* pi)) * 2.0 / 3.0;
			ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
			ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0	* pi)) * 2.0 / 3.0;
			return ret;
		}
	}
}
