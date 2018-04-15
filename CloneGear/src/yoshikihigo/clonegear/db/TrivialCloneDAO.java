package yoshikihigo.clonegear.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yoshikihigo.clonegear.CGConfig;
import yoshikihigo.clonegear.data.MD5;
import yoshikihigo.clonegear.data.TrivialClone;
import yoshikihigo.clonegear.gui.data.clone.GUICloneSet;

public class TrivialCloneDAO {

	static private TrivialCloneDAO SINGLETON = null;

	static private final String CLONETEXT_SCHEMA = "hash blob, text string, primary key(hash)";
	static private final String CLONEDISTRIBUTION_SCHEMA = "hash blob, project string, frequency integer, primary key(hash, project)";

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
	private String projectName;
	private Connection connector;

	private TrivialCloneDAO() {

		try {
			Class.forName("org.sqlite.JDBC");
			this.database = CGConfig.getInstance().getDATABASE();
			this.connector = DriverManager.getConnection("jdbc:sqlite:" + database);
			this.projectName = CGConfig.getInstance().getPROJETNAME();
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

	public void makeDB(final boolean force) {

		try {
			final Statement statement = this.connector.createStatement();
			if (force) {
				statement.executeUpdate("drop index if exists index_hash_clonetext");
				statement.executeUpdate("drop index if exists index_text_clonetext");
				statement.executeUpdate("drop table if exists clonetext");

				statement.executeUpdate("drop index if exists index_hash_clonedistribution");
				statement.executeUpdate("drop index if exists index_project_clonedistribution");
				statement.executeUpdate("drop index if exists index_frequency_clonedistribution");
				statement.executeUpdate("drop table if exists clonedistribution");
			}

			statement.executeUpdate("create table if not exists clonetext (" + CLONETEXT_SCHEMA + ")");
			statement.executeUpdate("create index if not exists index_hash_clonetext on clonetext(hash)");
			statement.executeUpdate("create index if not exists index_text_clonetext on clonetext(text)");
			statement.close();

			statement.executeUpdate("create table if not exists clonedistribution (" + CLONEDISTRIBUTION_SCHEMA + ")");
			statement.executeUpdate("create index if not exists index_hash_clonedistribution on clonetext(hash)");
			statement.executeUpdate(
					"create index if not exists index_project_clonedistribution on clonedistribution(project)");
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void registerCloneText(final GUICloneSet[] clonesets) {

		try {
			final PreparedStatement statement1 = this.connector
					.prepareStatement("replace into clonetext (hash,text) values (?, ?)");
			final PreparedStatement statement2 = this.connector
					.prepareStatement("replace into clonedistribution (hash, project, frequency) values (?, ?, ?)");

			int number = 0;
			for (final GUICloneSet c : clonesets) {
				final String code = c.getCode();
				final MD5 md5 = MD5.getMD5(code);
				final int frequency = c.size();

				statement1.setBytes(1, md5.value);
				statement1.setString(2, code);
				statement1.addBatch();

				statement2.setBytes(1, md5.value);
				statement2.setString(2, this.projectName);
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

	public List<TrivialClone> retrieveCloneText(){
		final List<TrivialClone> clones = new ArrayList<>();		
		try {
			final Statement statement = this.connector.createStatement();
			final String sqlText = "select t.text, count(d.hash), sum(d.frequency) from clonetext t " + 
					"inner join clonedistribution d on t.hash=d.hash group by d.hash;";
			final ResultSet results = statement.executeQuery(sqlText);

			while (results.next()) {
				final String text = results.getString(1);
				final int projects = results.getInt(2);
				final int frequency = results.getInt(3);
				final TrivialClone clone = new TrivialClone(text, projects, frequency);
				clones.add(clone);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return clones;
	}
}