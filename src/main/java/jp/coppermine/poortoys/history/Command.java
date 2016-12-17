package jp.coppermine.poortoys.history;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import jp.coppermine.poortoys.text.CsvCodec;

public class Command {

	/**
	 * Representation of a command body.
	 */
	private final String command;
	
	/**
	 * Representation of a command timestamp.
	 */
	private final LocalDateTime timestamp;
	
	/**
	 * Construct a command.
	 * 
	 * @param command command body, not null
	 * @param timestamp create at, not null
	 */
	private Command(String command, LocalDateTime timestamp) {
		this.command = command;
		this.timestamp = timestamp;
	}
	
	/**
	 * Create a command with the current time.
	 * 
	 * @param command command body, not null
	 * @return a command
	 */
	public static Command of(CharSequence command) {
		requireNonNull(command);
		return new Command(command.toString(), LocalDateTime.now());
	}
	
	/**
	 * Create a command with created time.
	 * 
	 * @param command command body, not null
	 * @param timestamp command timestamp, not null
	 * @return a command
	 */
	public static Command of(CharSequence command, LocalDateTime timestamp) {
		requireNonNull(command);
		requireNonNull(timestamp);
		return new Command(command.toString(), timestamp);
	}

	/**
	 * Deserializes a command from CSV format.
	 * 
	 * @param csv serialized command (CSV format)
	 * @return a command
	 */
	public static Command parse(CharSequence csv) {
		requireNonNull(csv);
		String[] fields = CsvCodec.decode(csv);
		return new Command(fields[1], LocalDateTime.parse(fields[0]));
	}
	
	/**
	 * Serializes a command to CSV format.
	 * 
	 * @return serialized command (CSV format)
	 */
	public String format() {
		return CsvCodec.encode(timestamp.toString(), command);
	}
	
	/**
	 * Obtains a command body.
	 * 
	 * @return a command body
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Obtains a command timestamp.
	 * 
	 * @return a command timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Command other = (Command) obj;
		if (command == null) {
			if (other.command != null) {
				return false;
			}
		} else if (!command.equals(other.command)) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return format();
	}
	
}
