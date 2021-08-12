package org.jdamico.rtl433toaprs;

public interface Constants {
	public static String JSON_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static int LAST_UPDATE_LIMIT = 3;
	public static String APP_NAME = "rtl433ToAprs";
	public static String DEFAULT_RTL_433_CLI = "rtl_433 -F json";
	public static String DEFAULT_RTL_TEST_CLI = "rtl_test -t";
	public static int USB_REST_TRIES = 5;
	public static String[] DEFAULT_DIGIPATH = new String[] {"WIDE1-1", "WIDE2-2"};
	public static String DEFAULT_RESET_RTL_CLI = "py_usb_reset.py";
	public static String DEFAULT_PYTHON_CLI = "python3";
}
