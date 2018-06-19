package ibsp.common.nio.core.util;

/**
 * Monitors uncaught exceptions. {@link #exceptionCaught(Throwable)} is invoked
 * when there are any uncaught exceptions.
 * <p>
 * You can monitor any uncaught exceptions by setting {@link ExceptionMonitor}
 * by calling {@link #setInstance(ExceptionMonitor)}. The default monitor logs
 * all caught exceptions in <tt>WARN</tt> level using SLF4J.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (旮� 13 7鞗�2007) $
 * 
 * @see DefaultExceptionMonitor
 */
public abstract class ExceptionMonitor {
	private static ExceptionMonitor instance = new DefaultExceptionMonitor();

	/**
	 * Returns the current exception monitor.
	 */
	public static ExceptionMonitor getInstance() {
		return instance;
	}

	/**
	 * Sets the uncaught exception monitor. If <code>null</code> is specified,
	 * the default monitor will be set.
	 * 
	 * @param monitor
	 *            A new instance of {@link DefaultExceptionMonitor} is set if
	 *            <tt>null</tt> is specified.
	 */
	public static void setInstance(ExceptionMonitor monitor) {
		if (monitor == null) {
			monitor = new DefaultExceptionMonitor();
		}
		instance = monitor;
	}

	/**
	 * Invoked when there are any uncaught exceptions.
	 */
	public abstract void exceptionCaught(Throwable cause);
}