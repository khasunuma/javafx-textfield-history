package jp.coppermine.poortoys.history;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Interface that provides command shell like history features.
 * It is designed that it assumes that implementations have history buffer in memory.
 * But it does not force to the implementations. 
 * <p>
 * it references Korn Shell's history features, but it has very restricted 
 * features for keep simple.
 *
 */
public interface History {
	
	/**
	 * Loads history data from its store to.
	 * <p>
	 * Loaded history is kept on the history buffer and never stored until 
	 * calling {@link #save()}.
	 */
	void load();
	
	/**
	 * Saves history data to its store.
	 * <p>
	 * The history loaded by {@link #load()} is kept on the history buffer 
	 * and never stored until calling this method.
	 */
	void save();
	
	/**
	 * Obtains the maximum size of the history buffer.
	 * 
	 * @return the maximum size of the history buffer, it must be 0 or above
	 */
	int getMaxSize();
	
	/**
	 * Obtains the list of commands from the loaded history.
	 * <p>
	 * Operations for history (<i>e.g.</i> obtains the current/next/previous 
	 * command) are provided by {@link List#listIterator()}.
	 * 
	 * @return the list of commands, never null
	 */
	List<Command> list();
	
	/**
	 * Add a command to the history buffer.
	 * <p>
	 * Even if this method is called, the history store is never modified. 
	 * 
	 * @param command a command that add to the history buffer, not null
	 */
	void append(Command command);
	
	/**
	 * Clear the history buffer.
	 * <p>
	 * Even if this method is called, the history store is never modified. 
	 */
	void clear();
	
	void shrink(LocalDateTime expired);
	
	/**
	 * A factory method that obtains one of {@code History} implementation as this class.
	 * 
	 * @param clazz the class object that is obtained the factory method, not null
	 * @return the implementation obtained
	 * @throws IllegalArgumentException the implementation is not found
	 */
	static <T extends History> History of(Class<T> clazz) {
		requireNonNull(clazz);
		
		List<History> instances = new ArrayList<>();
		ServiceLoader<History> loader = ServiceLoader.load(History.class);
		loader.forEach(instances::add);
		return instances.stream().filter(e -> e.getClass() == clazz).findFirst().orElseThrow(IllegalArgumentException::new);
	}
	
	/**
	 * A factory method that obtains the default {@code History} implementation.
	 * <p>
	 * In this version, the default implementation is {@link FileHistory}.
	 * 
	 * @return the default implements of this interface, never null
	 */
	static History getDefault() {
		return of(FileHistory.class);
	}
	
}
