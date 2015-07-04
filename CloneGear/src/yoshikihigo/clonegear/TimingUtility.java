package yoshikihigo.clonegear;

public class TimingUtility {

	public static String getExecutionTime(final long startTime,
			final long endTime) {

		final long nano = endTime - startTime;
		return getExecutionTime(nano);
	}
	
	public static String getExecutionTime(final long nano){
		
		final long micro = nano / 1000l;
		final long milli = micro / 1000l;
		final long second = milli / 1000l;

		final long hours = second / 3600;
		final long minutes = (second % 3600) / 60;
		final long seconds = (second % 3600) % 60;

		final StringBuilder text = new StringBuilder();
		if (0 < hours) {
			text.append(hours);
			text.append(" hours ");
		}
		if (0 < minutes) {
			text.append(minutes);
			text.append(" minutes ");
		}
		text.append(seconds);
		text.append(" seconds ");

		return text.toString();
	}
}
