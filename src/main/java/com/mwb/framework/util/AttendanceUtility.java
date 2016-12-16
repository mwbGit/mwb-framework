package com.mwb.framework.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class AttendanceUtility {
	
	public static final BigDecimal ONE_DAY = new BigDecimal("1");
	public static final BigDecimal HALF_DAY = new BigDecimal("0.5");
	public static final BigDecimal SIXTY_MINUTE = new BigDecimal("60");
	
	// 1分钟对应的毫秒
	public final static BigDecimal ONE_MINUTE = new BigDecimal(60 * 1000);
	// 早班休息的时间
	public final static int MORNING_SHIFT_TIME_REST_MINS = 90;
	// 中班休息的时间
	public final static int MIDDLE_SHIFT_TIME_REST_MINS = 60;

	
	public static final String DAY_STR = "天";
	public static final String HOUR_STR = "小时";
	public static final String MINS_STR = "分";
	public static final String HALF_DAY_STR = "0.5天";
	public static final String ONE_DAY_STR = "1天";
	public static final String SECONDS_STR  = ":00";
	
	// 上午请假开始时间
	public final static String AM_LEAVE_FROM_TIME = "00:00:00";
	
	
	/**
	 * 考勤日期attendanceDate是否在考勤周期内
	 * 
	 * @param attendanceDate
	 * @param attendanceEndDayOfMonth
	 * @return
	 */
	public static boolean isInCycleOfAttendance(Date attendanceDate, int attendanceEndDayOfMonth) {
		return isInCycleOfAttendance(attendanceDate, attendanceEndDayOfMonth, false);
	}
	
	/**
	 * 是否可创建考勤
	 * 
	 * @param attendanceDate
	 * @param attendanceEndDayOfMonth
	 * @return
	 */
	public static boolean isCreateAttendance(Date attendanceDate, int attendanceEndDayOfMonth) {
		return isInCycleOfAttendance(attendanceDate, attendanceEndDayOfMonth, true);
	}
	
	/**
	 * 考勤日期attendanceDate是否在考勤周期内
	 * 
	 * @param attendanceDate 考勤周期
	 * @param attendanceEndDayOfMonth 每月截止考勤的天
	 * @param addAttendance 是否是添加考勤
	 * @return
	 */
	private static boolean isInCycleOfAttendance(Date attendanceDate, int attendanceEndDayOfMonth, boolean addAttendance) {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		// 是否在周期内
		boolean inCycle = false;
		if (now.compareTo(attendanceDate) < 0) {
			// 当前日期小于考勤日期, 不可添加小于当前时间的考勤
			inCycle = !addAttendance;
		} else {
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int months = DateTimeUtility.monthsBetween(now, attendanceDate, false);
			// 当前时间是否在1号- 5号之间
			if (dayOfMonth < attendanceEndDayOfMonth) {
				// eg:当前时间为7月3号  可以修改6月的考勤,但不能修改6月之前的考勤
				// 可以修改上个月的考勤,但不能修改上个月之前的考勤
				// 0< between <= 1个月可以进行修改,否则不能修改
				if (months >= 0 && months <= 1) {
					inCycle = true;
				}
			} else {
				// eg:当前时间为6月11号 不能修改5月的考勤,但可以修改6月5号-6月11号
				// between == 0 可以进行修改,否则不能修改
				if (months == 0) {
					inCycle = true;
				}
			}
		}
		
		return inCycle;
	}
}
