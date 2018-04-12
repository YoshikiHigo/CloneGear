package yoshikihigo.clonegear.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.data.MD5;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;

public class TrivialCloneDAO {

	static private TrivialCloneDAO SINGLETON = null;

	static public TrivialCloneDAO getInstance() {
		if (null == SINGLETON) {
			SINGLETON = new TrivialCloneDAO();
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

	private TrivialCloneDAO() {

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

		final String CLONETEXT_SCHEMA = "hash blog, text string, primary key(blog)";
		final String CLONEDISTRIBUTION_SCHEMA = "hash blog, project string, frequency integer, primary key(hash, project)";

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

			statement.executeUpdate("create table clonedistribution (" + CLONEDISTRIBUTION_SCHEMA + ")");
			statement.executeUpdate("create index index_hash_clonedistribution on clonetext(hash)");
			statement.executeUpdate("create index index_project_clonedistribution on clonetext(project)");
			statement.close();

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void registerCloneText(final List<GUICloneSet> clonesets, final String projectName) {

		try {
			final PreparedStatement statement1 = this.connector.prepareStatement("replace into clonetext (?, ?)");
			final PreparedStatement statement2 = this.connector.prepareStatement("replace into clonetext (?, ?, ?)");

			int number = 0;
			for (final GUICloneSet c : clonesets) {
				final String code = c.getCode();
				final MD5 md5 = MD5.getMD5(code);
				final int frequency = c.size();

				statement1.setBytes(1, md5.value);
				statement1.setString(2, code);
				statement1.addBatch();

				statement2.setBytes(1, md5.value);
				statement2.setString(2, projectName);
				statement2.setInt(3, frequency);
				statement2.addBatch();

				number++;

				if (number > 10000) {
					statement1.executeBatch();
					statement2.executeBatch();
				}
			}
			statement1.executeBatch();
			statement2.executeBatch();

			statement1.close();
			statement2.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}