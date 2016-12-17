package jp.coppermine.poortoys.history;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.Collections.reverse;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * An implementation of {@link History} storing text file.
 * This is most similar to Korn Shell history features.
 *
 */
public class FileHistory extends AbstractHistory {

    public static final String PROPERTY_KEY_HISTORY_FILE_PATH = "poortoys.history.file.path";
    
    public static final String PROPERTY_KEY_HISTORY_FILE_CHARSET = "poortoys.history.file.charset";
	
    
    /**
	 * The default path to history file.
	 * There is not the default value of it.
	 * <p>
	 * It is determined by system property {@code poortoys.history.file.path}.
	 * 
	 */
    public static final Path DEFAULT_HISTORY_PATH =
            Paths.get(System.getProperty(PROPERTY_KEY_HISTORY_FILE_PATH, Paths.get(System.getProperty("user.home"), ".history").toString()));
	
    @Deprecated
    public static final Path HISTORY_FILE_FROM_PROPERTY = DEFAULT_HISTORY_PATH;
	
	/**
	 * The default character set of history file.
	 * The default value is {@code UTF-8}.
	 */
    public static final Charset DEFAULT_HISTORY_CHARSET = 
            Charset.forName(System.getProperty(PROPERTY_KEY_HISTORY_FILE_CHARSET, "UTF-8"));
    
    @Deprecated
	public static final Charset HISTORY_CHARSET_FROM_PROPERTY = DEFAULT_HISTORY_CHARSET;
	
	/**
	 * Path to the history file.
	 * <p>
	 * If using system property, set it to {@code poortoys.history.file.path}.
	 */
	private final Path path;
	
	/**
	 * Character set of the history file.
	 * <p>
	 * The default value is {@code Charset.forName("UTF-8")}.
	 * <p>
	 * If using system property, set it to {@code poortoys.history.file.charset}.
	 */
	private final Charset charset;
	
	/**
	 * Creates an instance of this class.
	 * {code path} is obtained by system property, {@code poortoys.file.path}
	 * {@code charset} is automatically set {@code UTF-8}.
	 * {@code maxSize} is automatically set {@code DEFAULT_HISTORY_SIZE}.
	 * 
	 */
	public FileHistory() {
		this(DEFAULT_HISTORY_PATH, DEFAULT_HISTORY_CHARSET, DEFAULT_HISTORY_SIZE);
	}
	
	/**
	 * Creates an instance of this class, provides {@code path}.
	 * {@code charset} is automatically set {@code UTF-8}.
	 * {@code maxSize} is automatically set {@code DEFAULT_HISTORY_SIZE}.
	 * 
	 * @param path the path to history file, not null
	 */
	public FileHistory(Path path) {
		this(path, DEFAULT_HISTORY_CHARSET, DEFAULT_HISTORY_SIZE);
	}
	
	/**
	 * Creates an instance of this class, provides {@code path} and {@code charset}.
	 * {@code maxSize} is automatically set {@code DEFAULT_HISTORY_SIZE}.
	 * 
	 * @param path the path to history file, not null
	 * @param charset character set of history file, default value is {@code UTF-8}
	 */
	public FileHistory(Path path, Charset charset) {
		this(path, charset, DEFAULT_HISTORY_SIZE);
	}
	
	/**
	 * Creates an instance of this class, provides {@code path} and {@code maxSize}.
	 * {@code charset} is automatically set {@code UTF-8}.
	 * 
	 * @param path the path to history file, not null
	 * @param maxSize the maximum size of history buffer, 0 or above
	 */
	public FileHistory(Path path, int maxSize) {
		this(path, DEFAULT_HISTORY_CHARSET, maxSize);
	}
	
	/**
	 * Creates an instance of this class, provides {@code path}, {@code charset} and {@code maxSize}.
	 * 
	 * @param path the path to history file, not null
	 * @param charset character set of history file, default value is {@code UTF-8}
	 * @param maxSize the maximum size of history buffer, 0 or above
	 */
	public FileHistory(Path path, Charset charset, int maxSize) {
		super(maxSize);
		this.path = path;
		this.charset = charset;
	}
	
	/**
	 * Obtains path to history file.
	 * 
	 * @return path to history file, never null.
	 */
	public Path getHistoryFilePath() {
		return path;
	}
	
	/**
	 * Obtains the character set of history file.
	 * 
	 * @return character set of history file, default value is {@code UTF-8}
	 */
	public Charset getCharset() {
		return charset;
	}

	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#load()
	 */
	@Override
	public synchronized void load() {
		try (Stream<String> lines = Files.lines(path, charset)) {
			List<Command> commands = lines
					.filter(e -> e != null)
					.map(Command::parse)
					.limit(getMaxSize())
					.collect(toList());
			reverse(commands);
			getCommands().clear();
			getCommands().addAll(commands);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/* (non-Javadoc)
	 * @see jp.coppermine.poortoys.history.History#save()
	 */
	@Override
	public synchronized void save() {
		try {
			List<String> commands = getCommands().stream()
					.filter(e -> e != null)
					.map(Command::format)
					.limit(getMaxSize())
					.collect(toList());
			reverse(commands);
			Files.write(path, commands, charset, CREATE, WRITE, TRUNCATE_EXISTING);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
}
