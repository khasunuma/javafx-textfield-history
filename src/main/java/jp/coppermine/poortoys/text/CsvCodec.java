package jp.coppermine.poortoys.text;

import static java.util.regex.Pattern.COMMENTS;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CsvCodec {
	
	/**
	 * Suppresses default constructor, ensuring non-instantiability.
	 */
	private CsvCodec() { }
	
	/**
	 * A pattern of regular expression for splitting a CSV field to entries.
	 */
	private static final String REGEX = "\\G(?:^|,) \n" + "(?: \n"
			+ "   # a field enclosed double-quotes ... \n"
			+ "   \" # an open quote of a field  \n"
			+ "    ( (?: [^\"]++ | \"\" | \n )*+ ) \n"
			+ "   \" # a close quote of a field \n" + " | # ... or ... \n"
			+ "      # ... a plain text not use quotes and commas \n"
			+ "   ( [^\",]*) \n" + " ) \n";

	/**
	 * Split a CSV field to entries.
	 * 
	 * @param field
	 *            a CSV field.
	 * @return a String array contains entries.
	 */
	public static String[] decode(CharSequence field) {
		// If the length of field is zero or field contents only spaces,
		// the method returns a String array it's length zero.
		// At first, checking field's length because of performance reason.
		// Because some "toString" method may spend large time,
		// so we will early out if not use a "toString".
		if (field.length() == 0 || field.toString().length() == 0) {
			return new String[0];
		}

		// Create a matcher object of the above regular expression,
		// at this time, use a dummy text.
		Matcher mMain = Pattern.compile(REGEX, COMMENTS).matcher("");

		// Create a matcher object of the regular expression '""' (two double-quotes),
		// at this time, use a dummy text.
		Matcher mQuote = Pattern.compile("\"\"").matcher("");

		// A list of strings for contains fields.
		List<String> entries = new ArrayList<String>();

		// A line by line, a following procedure.
		mMain.reset(field);
		while (mMain.find()) {
			String entry;
			if (mMain.start(2) >= 0) {
				// A string not enclosed quotes.
				// Trim blank from it.
				entry = mMain.group(2).trim();
			} else {
				// A string enclosed two quotes.
				// Replace Two double-quotes to one single quote.
				entry = mQuote.reset(mMain.group(1)).replaceAll("\"");
			}
			entries.add(entry);
		}

		if (!field.toString().trim().isEmpty() && (entries.size() == 1 && entries.get(0).isEmpty())) {
			throw new CsvFormatException();
		}

		// It exchanges the list to an array and returns it.
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * Construct a CSV field from an entries.
	 * 
	 * @param entries
	 *            an entries of CSV, character sequences.
	 * @return a CSV field.
	 */
	public static String encode(CharSequence... entries) {
		List<String> entryList = new ArrayList<String>(entries.length);

		// Create a matcher object of no-escaped entries.
		// At this time, use a dummy text.
		Matcher mUnescape = Pattern.compile("^(?=\\S)[^\"\n\t ,]+(?<=\\S)$").matcher("");

		// Create a matcher object of the regular expression '""' (two
		// double-quotes),
		// at this time, use a dummy text.
		Matcher mQuote = Pattern.compile("\"").matcher("");

		for (CharSequence entry : entries) {
			// A string consists one character and not contains WSChars.
			mUnescape.reset(entry);
			if (mUnescape.matches()) {
				entryList.add(entry.toString());
				continue;
			}

			// An escaped string.
			// Replace one double-quote to two double-quotes.
			entryList.add("\"" + mQuote.reset(entry).replaceAll("\"\"") + "\"");
		}

		// Join entries delimited commas, and return it.
		return String.join(",", entryList);
	}
	
}
