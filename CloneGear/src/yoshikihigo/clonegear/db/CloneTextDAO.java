package yoshikihigo.clonegear.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import yoshikihigo.clonegear.CGConfig;

public class CloneTextDAO {

	static private CloneTextDAO SINGLETON = null;

	static public CloneTextDAO getInstance() {
		if (null == SINGLETON) {
			SINGLETON = new CloneTextDAO();
		}
		return SINGLETON;
	}

	static public void deleteInstance() throws Exception {
		if (null != SINGLETON) {
			SINGLETON.clone();
			SINGLETON = null;
		}
	}

	private String database;
	private Connection connector;

	private CloneTextDAO() {

		try {
			Class.forName("org.sqlite.JDBC");
			this.database = CGConfig.getInstance().getDATABASE();
			this.connector = DriverManager.getConnection("jdbc:sqlite:" + database);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void close() {
		try {
			this.connector.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void makeDB() {

		final String CLONETEXT_SCHEMA = "hash blog primary key, text string";
		final String CLONEDISTRIBUTION_SCHEMA = "hash blog, project string, frequency integer";

		try {
			Class.forName("org.sqlite.JDBC");
			final Connection connector = DriverManager.getConnection("jdbc:sqlite:" + this.database);

			final Statement statement = connector.createStatement();
			statement.executeUpdate("drop index if exists index_hash_clonetext");
			statement.executeUpdate("drop index if exists index_text_clonetext");
			statement.executeUpdate("drop table if exists clonetext");

			statement.executeUpdate("drop index if exists index_hash_clonedistribution");
			statement.executeUpdate("drop index if exists index_project_clonedistribution");
			statement.executeUpdate("drop index if exists index_frequency_clonedistribution");
			statement.executeUpdate("drop table if exists clonedistribution");

			statement.executeUpdate("create table clonetext (" + CLONETEXT_SCHEMA + ")");
			statement.executeUpdate("create index index_hash_clonetext on clonetext(hash)");
			statement.executeUpdate("create index index_text_clonetext on clonetext(text)");
			statement.close();

			statement.executeUpdate("create table clonedistribution (" + CLONETEXT_SCHEMA + ")");
			statement.executeUpdate("create index index_hash_clonedistribution on clonetext(hash)");
			statement.executeUpdate("create index index_project_clonedistribution on clonetext(project)");
			statement.close();

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void registerCloneText(final List<GUICloneSet> clonesets){
			final Statement statement2 = connector.createStatement();
			final ResultSet results2 = statement2
					.executeQuery("select software, id, date, message, author from revisions");
			final PreparedStatement statement3 = connector
					.prepareStatement("insert into bugfixrevisions values (?, ?, ?, ?, ?, ?, ?)");
			while (results2.next()) {
				final String software = results2.getString(1);
				final String id = results2.getString(2);
				final String date = results2.getString(3);
				final String message = results2.getString(4);
				final String author = results2.getString(5);

				int bugfix = 0;
				final StringBuilder urls = new StringBuilder();
				for (final Entry<String, String> entry : bugIDs.entrySet()) {
					final String bugId = entry.getKey();
					if (message.contains(bugId)) {
						bugfix++;
						final String url = entry.getValue();
						urls.append(url);
						urls.append(System.lineSeparator());
					}
				}

				statement3.setString(1, software);
				statement3.setString(2, id);
				statement3.setString(3, date);
				statement3.setString(4, message);
				statement3.setString(5, author);
				statement3.setInt(6, bugfix);
				statement3.setString(7, urls.toString());
				statement3.executeUpdate();
			}
			statement2.close();
			statement3.close();

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}