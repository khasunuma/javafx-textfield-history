package jp.coppermine.poortoys.history;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

/**
 * Core implementation of {@link History}.
 * <p>
 * It implements mainly operation of history buffer.
 * Specified classes must override {@link #load()} and {@link #save} to access the store.
 * <p>
 * The maximum size of history buffer is determined by the argument of constructor.
 * It is detected following;
 * <ol>
 * <li>if it is constructed by {@link #AbstractHistory(int)}, it uses the arguments.
 * but the arguments is below to zero, it uses {@link #DEFAULT_HISTORY_SIZE} alternately.</li>
 * <li>if it is constructed by {@link #AbstractHistory()}, it tries to use the system 
 * property {@code poortoys.history.size}. if it is 0 or above, using property value.
 * Otherwise, using {@link #DEFAULT_HISTORY_SIZE} alternately</li>
 * </ol>
 * 
 */
public abstract class AbstractHistory implements History {
	
    /**
     * Property key to set specified history size.
     * This value is "poortoys.history.size"
     */
    public static final String PROPERTY_KEY_HISTORY_SIZE = "poortoys.history.size";
    
	/**
	 * The default size of the history buffer.
	 */
	public static final int DEFAULT_HISTORY_SIZE = 128;
	
	/**
	 * The maximum size of the history buffer.
	 */
	private final int maxSize;
	
	/**
	 * Representation of the command history;
	 */
	private List<Command> commands;
	
	/**
	 * Creates an instance of this class by system property.
	 * <p>
	 * If system property {@code poortoys.history.size} is defined, it uses the value.
	 * Otherwise it uses {@link #DEFAULT_HISTORY_SIZE}.
	 * And the property value is invalid (<i>i.g.</i> negative number), it also uses
	 * {@link #DEFAULT_HISTORY_SIZE} alternatively.
	 */
	protected AbstractHistory() {
		this(Integer.getInteger(PROPERTY_KEY_HISTORY_SIZE, DEFAULT_HISTORY_SIZE).intValue());
	}
	
	/**
	 * Creates an instance of this class by {@code maxCode}.
	 * <p>
	 * if {@code maxSize} is below from 0, it uses {@link #DEFAULT_HISTORY_SIZE} alternatively.
	 * 
	 * @param maxSize the maximum size of history buffer
	 */
	protected AbstractHistory(int maxSize) {
		this.maxSize = maxSize < 0 ? DEFAULT_HISTORY_SIZE : maxSize;
		this.commands = new CopyOnWriteArrayList<>();
	}
	
	/**
	 * Obtains the command history in direct.
	 * To different from {@link #list()}, it's return object is modifiable.
	 * 
	 * @return the command history, never null 
	 */
	protected List<Command> getCommands() {
		return commands;
	}
	
	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#getMaxSize()
	 */
	@Override
	public int getMaxSize() {
		return maxSize;
	}

	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#list()
	 */
	@Override
	public List<Command> list() {
		return unmodifiableList(getCommands());
	}

	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#append(java.lang.CharSequence)
	 */
	@Override
	public synchronized void append(Command command) {
		List<Command> commands = Stream.concat(Stream.of(command), getCommands().stream()).limit(maxSize).collect(toList());
		getCommands().clear();
		getCommands().addAll(commands);
	}

	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#clear()
	 */
	@Override
	public synchronized void clear() {
		getCommands().clear();
	}

	@Override
	public void shrink(LocalDateTime expired) {
		List<Command> commands = getCommands().stream().filter(e -> e.getTimestamp().isAfter(expired)).collect(toList());
		getCommands().clear();
		getCommands().addAll(commands);
	}
	
}
