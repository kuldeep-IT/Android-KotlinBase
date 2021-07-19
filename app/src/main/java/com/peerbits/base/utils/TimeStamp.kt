package com.nlgic.insurance.utils

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import com.peerbits.base.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import timber.log.Timber

class TimeStamp {

    companion object {

        const val FULL_DATE_FORMAT = "MMM dd yyyy, hh:mm:ss a"
        const val FULL_DATE_TIME_FORMAT = "MMM dd yyyy, hh:mm a"

        @JvmStatic
        val FULL_DATE_T_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        const val DATE_FORMAT_DDMMYYYY = "dd-MM-yyyy"
        const val DATE_FORMAT_REGISTER = "yyyy-MM-dd"
        const val TIME_FORMAT = "hh:mm:ss a"
        const val TIME_FORMAT_COUNTDOWN = "d HH mm"
        const val TIME_FORMAT_FOR_SHIFT = "hh:mm a"

        @JvmStatic
        val DATE_FORMAT_TO_SHOW = "dd-MMM-yyyy"
        val DATE_FORMAT_WITH_TIME = "dd-MMM-yyyy hh:mm a"
        const val DATE_FORMAT_YYMMDD = "yyyyMMdd"
        const val TORO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        fun formatToSeconds(value: String, format: String): Long {
            val timeZone = TimeZone.getDefault()
            return formatToSeconds(value, format, timeZone)
        }

        fun formatToMilliSec(value: String, format: String = FULL_DATE_T_TIME_FORMAT): Long {
            val timeZone = TimeZone.getDefault()
            return formatToMilliSec(value, format, timeZone)
        }

        fun formatToDate(value: String, format: String): Date {
            val timeZone = TimeZone.getDefault()
            return if (value.isBlank()) Date() else Date(formatToMilliSec(value, format, timeZone))
        }

        fun formatToSecondsWeather(value: String, format: String): Long {
            val timeZone = TimeZone.getDefault()
            return formatToSecondsWeather(value, format, timeZone)
        }

        fun formatToSecondsUTC(value: String, format: String): Long {
            val timeZone = TimeZone.getTimeZone("GMT")
            return formatToSeconds(value, format, timeZone) + 43200
        }

        fun secondsToSecondsLocalOnePM(value: Long): Long {
            return value + 46800
        }

        fun formatToSecondsLocalOnePM(value: String, format: String): Long {
            return formatToSeconds(value, format) + 46800
        }

        fun formatToSecondsMidnightUTC(value: String, format: String): Long {
            val timeZone = TimeZone.getTimeZone("GMT")
            return formatToSeconds(value, format, timeZone)
        }

        fun formatToSeconds(value: String, format: String, timeZone: TimeZone): Long {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.timeZone = timeZone
                val mDate = sdf.parse(value)
                return TimeUnit.MILLISECONDS.toSeconds(mDate.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }

        fun formatToMilliSec(
            value: String,
            format: String = FULL_DATE_T_TIME_FORMAT,
            timeZone: TimeZone = TimeZone.getDefault()
        ): Long {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = timeZone
                val mDate = sdf.parse(value)
                return mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }

        fun formatToSecondsWeather(value: String, format: String, timeZone: TimeZone): Long {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.timeZone = timeZone
                val mDate = sdf.parse(value)
                return TimeUnit.MILLISECONDS.toSeconds(mDate.time) + 46800
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }

        fun millisToFormat(millis: Long?): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormat(millis!!, FULL_DATE_FORMAT, timeZone)
        }

        fun millisToFormat(millis: String): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormat(java.lang.Long.parseLong(millis), FULL_DATE_FORMAT, timeZone)
        }

        fun millisToTimeFormat(millis: String): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormat(java.lang.Long.parseLong(millis), TIME_FORMAT_FOR_SHIFT, timeZone)
        }

        fun millisToFormat(millis: Long, format: String = FULL_DATE_T_TIME_FORMAT): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormat(millis, format, timeZone)
        }

        fun millisToFormatEnglish(millis: Long, format: String = FULL_DATE_T_TIME_FORMAT): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormatEnglish(millis, format, timeZone)
        }

        fun millisToFormatInUTC(millis: Long, format: String): String {
            val timeZone = TimeZone.getTimeZone("GMT")
            return millisToFormat(millis, format, timeZone)
        }

        fun millisToFormat(millis: String, format: String): String {
            val timeZone = TimeZone.getDefault()
            return millisToFormat(java.lang.Long.parseLong(millis), format, timeZone)
        }

        fun getUTCTimstamp(timeSmap: Long): Long {
            var timeSmap = timeSmap
            if (timeSmap < 1000000000000L) {
                timeSmap *= 1000
            }
            val offset = TimeZone.getDefault().rawOffset + TimeZone.getDefault().dstSavings
            timeSmap = timeSmap + offset
            return timeSmap
        }

        fun millisToFormat(millis: Long, format: String, tz: TimeZone): String {
            var millis = millis

            /*if (millis < 1000000000000L) {
                millis *= 1000
            }*/

            val cal = Calendar.getInstance(tz)
            cal.timeInMillis = millis
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = tz
            return sdf.format(cal.time)
        }

        fun millisToFormatEnglish(millis: Long, format: String, tz: TimeZone): String {
            var millis = millis

            /*if (millis < 1000000000000L) {
                millis *= 1000
            }*/

            val cal = Calendar.getInstance(tz)
            cal.timeInMillis = millis
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            sdf.timeZone = tz
            return sdf.format(cal.time)
        }

        fun getYTT(millis: Long): String {
            var millis = millis
            if (millis < 1000000000000L) {
                millis *= 1000
            }
            val givenTime = Calendar.getInstance()
            givenTime.timeInMillis = millis
            Timber.e("Given Time", givenTime.time.toString())

            val now = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DATE, +1)

            if (givenTime.time == now.time) {
                return "Today"
            } else if (givenTime.time == yesterday.time) {
                return "Yesterday"
            } else if (givenTime.time == tomorrow.time) {
                return "Tomorrow"
            } else {
                val date = DateFormat.format("EEE dd, MMM yyyy", givenTime).toString()
                return String.format("%s", date)
            }
        }

        fun formatDate(milliSec: Long): String {
            val SECONDS_IN_A_DAY = 24 * 60 * 60

            val diff = milliSec - System.currentTimeMillis()
            val diffSec = diff / 1000

            val days = diffSec / SECONDS_IN_A_DAY
            val secondsDay = diffSec % SECONDS_IN_A_DAY
            val seconds = secondsDay % 60
            val minutes = secondsDay / 60 % 60
            val hours = secondsDay / 3600 // % 24 not needed

            return "$days $hours $minutes"
        }

        fun formatDate(
            value: String?,
            inputFormat: String = TORO_DATE_FORMAT,
            outputFormat: String = TORO_DATE_FORMAT
        ): String {

            if (value.isNullOrBlank())
                return ""

            val timeZone = TimeZone.getTimeZone("GMT")
            val date = Date(formatToMilliSec(value, inputFormat, timeZone))
            return millisToFormat(date.time, outputFormat)
        }

        @JvmStatic
        fun formatDateLocal(
            value: String,
            inputFormat: String = FULL_DATE_T_TIME_FORMAT,
            outputFormat: String = DATE_FORMAT_TO_SHOW
        ): String {

            if (value.isEmpty())
                return ""

            val timeZone = TimeZone.getDefault()
            val date = Date(formatToMilliSec(value, inputFormat, timeZone))
            if (outputFormat == FULL_DATE_T_TIME_FORMAT)
                return millisToFormatEnglish(date.time, outputFormat)
            else
                return millisToFormat(date.time, outputFormat)
        }

        @JvmStatic
        fun formatTimeLocal(
            value: String,
            inputFormat: String = FULL_DATE_T_TIME_FORMAT,
            outputFormat: String = TIME_FORMAT_FOR_SHIFT
        ): String {

            if (value.isEmpty())
                return ""

            val timeZone = TimeZone.getDefault()
            val date = Date(formatToMilliSec(value, inputFormat, timeZone))
            return millisToFormat(date.time, outputFormat)
        }

        fun getTimeAgo(time: Long, mContext: Context): String {
            var time = time
            if (time < 1000000000000L) {
                time *= 1000
            }
            val SECOND_MILLIS = 1000
            val MINUTE_MILLIS = 60 * SECOND_MILLIS
            val HOUR_MILLIS = 60 * MINUTE_MILLIS
            val DAY_MILLIS = 24 * HOUR_MILLIS

            val cal = Calendar.getInstance()
//        cal.timeZone = TimeZone.getTimeZone("GMT")

            val now = cal.timeInMillis
            if (time > now || time <= 0) {
                return mContext.getString(R.string.str_in_the_future)
            }

            val diff = now - time
            return if (diff < MINUTE_MILLIS) {
                mContext.getString(R.string.str_moments_ago)
            } else if (diff < 2 * MINUTE_MILLIS) {
                mContext.getString(R.string.str_a_minute_ago)
            } else if (diff < 50 * MINUTE_MILLIS) {
                (diff / MINUTE_MILLIS).toString() + mContext.getString(R.string.str_minutes_ago)
            } else if (diff < 90 * MINUTE_MILLIS) {
                mContext.getString(R.string.str_an_hour_ago)
            } else if (diff < 24 * HOUR_MILLIS) {
                (diff / HOUR_MILLIS).toString() + mContext.getString(R.string.str_hours_ago)
            } else if (diff < 48 * HOUR_MILLIS) {
                mContext.getString(R.string.str_yesterday)
            } else {
                (diff / DAY_MILLIS).toString() + mContext.getString(R.string.str_days_ago)
            }
        }

        fun getDateToShowInUTC(millis: Long, format: String): String {
            var millis = millis
            if (millis < 1000000000000L) {
                millis *= 1000
            }
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            return sdf.format(millis)
        }

        fun timeToSeconds(givenDateString: String): Long {
            try {
                val getTime =
                    givenDateString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val hours: Long
                val mins: Long
                val newTime =
                    getTime[0].split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (getTime[1].equals("am", ignoreCase = true)) {
                    if (Integer.parseInt(newTime[0]) == 12)
                        hours = (java.lang.Long.parseLong(newTime[0]) - 12) * 60 * 60
                    else
                        hours = java.lang.Long.parseLong(newTime[0]) * 60 * 60

                    mins = java.lang.Long.parseLong(newTime[1]) * 60
                } else {
                    if (Integer.parseInt(newTime[0]) == 12)
                        hours = java.lang.Long.parseLong(newTime[0]) * 60 * 60
                    else
                        hours = (java.lang.Long.parseLong(newTime[0]) + 12) * 60 * 60

                    mins = java.lang.Long.parseLong(newTime[1]) * 60
                }
                return hours + mins
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return 0
        }

        fun dateToSecondsInUTC(givenDateString: String): Long {
            try {
                val sdf = SimpleDateFormat("MMM dd yyyy")
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val mDate = sdf.parse(givenDateString)
                return TimeUnit.MILLISECONDS.toSeconds(mDate.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }

        fun dateToSeconds(givenDateString: String): Long {
            try {
                val sdf = SimpleDateFormat("MMM dd yyyy")
                sdf.timeZone = TimeZone.getDefault()
                val mDate = sdf.parse(givenDateString)
                return TimeUnit.MILLISECONDS.toSeconds(mDate.time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return 0
        }

        fun toUTC(date: String, format: String): Long {

            val timestamp = formatToSeconds(date, format)

            return toUTC(timestamp)
        }

        fun toUTCMidnight(date: String, format: String): Long {

            val timestamp = formatToSeconds(date, format)

            return toUTCMidnight(timestamp)
        }

        fun toUTCMidnight(timestamp: Long): Long {
            var timestamp = timestamp

            if (timestamp < 1000000000000L) {
                timestamp *= 1000
            }
            val cal = Calendar.getInstance()
            val offset = cal.timeZone.getOffset(timestamp)
            return timestamp + offset
        }

        fun toUTC(timestamp: Long): Long {
            var timestamp = timestamp

            if (timestamp < 1000000000000L) {
                timestamp *= 1000
            }
            val cal = Calendar.getInstance()
            val offset = cal.timeZone.getOffset(timestamp)
            return timestamp + offset.toLong() + 43200000
        }

        fun fromUTC(timestamp: Long): Long {

            var timestamp = timestamp

            if (timestamp < 1000000000000L) {
                timestamp *= 1000
            }
            val cal = Calendar.getInstance()
            val offset = cal.timeZone.getOffset(timestamp)
            return timestamp - offset
        }

        fun getDateFromTimestamp(milliSeconds: Long): String {

            var milliSeconds = milliSeconds

            if (milliSeconds < 1000000000000L) {
                milliSeconds *= 1000
            }
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            return formatter.format(Date(milliSeconds))
        }

        fun timestampToDate(timestamp: Long?): String {

            var timestamp = timestamp

            if (timestamp!! < 1000000000000L) {
                timestamp *= 1000
            }

            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            return DateFormat.format("dd MMM,", cal)
                .toString() + " at " + DateFormat.format("hh:mm a ", cal).toString()
        }

        fun getAge(insuredDOB: String): String? {
            val dobYear = formatDateLocal(insuredDOB, outputFormat = "yyyy")
            val cal = Calendar.getInstance()
            val age = cal.get(Calendar.YEAR) - dobYear.toInt()
            return age.toString()
        }

        fun getMillisDiff(insuredDOB: String, coverFrom: String): Long {
            val dob = formatToMilliSec(insuredDOB, FULL_DATE_T_TIME_FORMAT)
            val cover = formatToMilliSec(coverFrom, FULL_DATE_T_TIME_FORMAT)
            val diff = cover - dob
            return diff
        }

        fun getDaysDifference(
            value: String?, value2: String?,
            inputFormat: String = FULL_DATE_T_TIME_FORMAT
        ): String {

            if (value.isNullOrBlank() && value2.isNullOrBlank())
                return ""

            val timeZone = TimeZone.getTimeZone("GMT")
            val date = Date(formatToMilliSec(value!!, inputFormat, timeZone))
            val date2 = Date(formatToMilliSec(value2!!, inputFormat, timeZone))
            val diff: Long = date2.getTime() - date.getTime()
            return (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1).toString()
        }
    }
}
