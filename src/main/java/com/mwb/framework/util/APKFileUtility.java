package com.mwb.framework.util;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;
import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import test.AXMLPrinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class APKFileUtility {
	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F };
    private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt", "in", "mm", "", "" };
    private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "", "", "" };
    
    public static String getVersionCode(String file) throws Exception {
        String xml = AXMLPrinter.getManifestXMLFromAPK(file);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Document dom = builder.parse(inputStream);
        
        //String versionName = dom.getDocumentElement().getAttribute("android:versionName");
        
        return dom.getDocumentElement().getAttribute("android:versionCode");
    }
    
    public static String getVersionCode1(String file) throws Exception {
    	return getAttributeValue(file, "versionCode");
    }
    
	public static String getVersionName(String file) throws Exception {
		return getAttributeValue(file, "versionName");
	}
    
	private static String getAttributeValue(String file, String attributeName) throws Exception {
		ZipFile zip = new ZipFile(new File(file));
		Enumeration<? extends ZipEntry> enume = zip.entries();

		String value = "";
		while (enume.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enume.nextElement();
			String filename = zipEntry.getName();
			if ("AndroidManifest.xml".equalsIgnoreCase(filename)) {
				InputStream zStream = zip.getInputStream(zipEntry);
				value = getAttributeValue(zStream, attributeName);
				zStream.close();
				break;
			}
		}
		zip.close();

		return value;
	}
	
    private static String getAttributeValue(InputStream is, String attributeName) throws Exception {
    	 
        String value = null;
        try {
            AXmlResourceParser parser = new AXmlResourceParser();
            parser.open(is);
            boolean brek = false;
            while (true) {
                int type = parser.next();
                if (type == XmlPullParser.END_DOCUMENT) {
                    break;
                }
                switch (type) {
                case XmlPullParser.START_TAG:
                    for (int i = 0; i != parser.getAttributeCount(); ++i) {
                        if (attributeName.equals(parser.getAttributeName(i))) {
                        	value = getAttributeValue(parser, i);
                            brek = true;
                            break;
                        }
                    }
                }
 
                if (brek) {
                    break;
                }
            }
            	
        } catch (Exception e) {

            throw e;
        }
 
        return value;
    }
 
    private static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        }
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        }
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        }
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        }
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        }
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data))
                    + DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data))
                    + FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        }
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT
                && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT
                && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }
 
    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }
    
    private static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }
}
