package smarticulous;

import smarticulous.db.Exercise;
import smarticulous.db.Exercise.Question;
import smarticulous.db.Submission;
import smarticulous.db.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * The Smarticulous class, implementing a grading system.
 */
public class Smarticulous {

    /**
     * The connection to the underlying DB.
     * <p>
     * null if the db has not yet been opened.
     */
    Connection db;

    /**
     * Open the {@link Smarticulous} SQLite database.
     * <p>
     * This should open the database, creating a new one if necessary, and set the {@link #db} field
     * to the new connection.
     * <p>
     * The open method should make sure the database contains the following tables, creating them if necessary:
     *
     * <table>
     *   <caption><em>Table name: <strong>User</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>UserId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Username</td><td>Text</td></tr>
     *   <tr><td>Firstname</td><td>Text</td></tr>
     *   <tr><td>Lastname</td><td>Text</td></tr>
     *   <tr><td>Password</td><td>Text</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Exercise</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>DueDate</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Question</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Name</td><td>Text</td></tr>
     *   <tr><td>Desc</td><td>Text</td></tr>
     *   <tr><td>Points</td><td>Integer</td></tr>
     * </table>
     * In this table the combination of ExerciseId and QuestionId together comprise the primary key.
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>Submission</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer (Primary Key)</td></tr>
     *   <tr><td>UserId</td><td>Integer</td></tr>
     *   <tr><td>ExerciseId</td><td>Integer</td></tr>
     *   <tr><td>SubmissionTime</td><td>Integer</td></tr>
     * </table>
     *
     * <p>
     * <table>
     *   <caption><em>Table name: <strong>QuestionGrade</strong></em></caption>
     *   <tr><th>Column</th><th>Type</th></tr>
     *   <tr><td>SubmissionId</td><td>Integer</td></tr>
     *   <tr><td>QuestionId</td><td>Integer</td></tr>
     *   <tr><td>Grade</td><td>Real</td></tr>
     * </table>
     * In this table the combination of SubmissionId and QuestionId together comprise the primary key.
     *
     * @param dburl The JDBC url of the database to open (will be of the form "jdbc:sqlite:...")
     * @return the new connection
     * @throws SQLException
     */
    public Connection openDB(String dburl) throws SQLException {
        // Define the schema for each table in the database
        String[][] tables = {
                {"User", "UserId INTEGER PRIMARY KEY, Username text UNIQUE", "Firstname text", "Lastname text", "Password text"},
                {"Exercise", "ExerciseId INTEGER PRIMARY KEY", "Name text", "DueDate INTEGER"},
                {"Question", "ExerciseId INTEGER", "QuestionId INTEGER", "Name text", "Desc text", "Points INTEGER", "PRIMARY KEY (ExerciseId, QuestionId)"},
                {"Submission", "SubmissionId INTEGER PRIMARY KEY", "UserId INTEGER", "ExerciseId INTEGER", "SubmissionTime INTEGER"},
                {"QuestionGrade", "SubmissionId INTEGER", "QuestionId INTEGER", "Grade REAL", "PRIMARY KEY (SubmissionId, QuestionId)"}
        };
        // Establish the connection to the database using the provided URL
        db = DriverManager.getConnection(dburl);
        // Iterate over the tables and create each one in the database
        for (String[] table : tables){
            createTable(db, table[0], table);
        }
        return db;
    }

    public void createTable(Connection connection, String tableName, String[] columns) throws SQLException{
        // Start building the SQL query to create the table
        StringBuilder createTableQuery = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        createTableQuery.append(tableName).append(" (");
        // Append each column definition to the query
        // Start at index 1 as index 0 is the table name
        for (int i = 1; i < columns.length; i++){
            createTableQuery.append(columns[i]);
            // Add a comma unless it's the last column
            if (i < columns.length - 1){
                createTableQuery.append(", ");
            }
        }
        createTableQuery.append(")"); // Close the SQL statement
        // Prepare and execute the SQL statement to create the table
        PreparedStatement statement = connection.prepareStatement(createTableQuery.toString());
        statement.executeUpdate();
    }


    /**
     * Close the DB if it is open.
     *
     * @throws SQLException
     */
    public void closeDB() throws SQLException {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    // =========== User Management =============

    /**
     * Add a user to the database / modify an existing user.
     * <p>
     * Add the user to the database if they don't exist. If a user with user.username does exist,
     * update their password and firstname/lastname in the database.
     *
     * @param user
     * @param password
     * @return the userid.
     * @throws SQLException
     */
    public int addOrUpdateUser(User user, String password) throws SQLException {
        int userId;
        userId = 0;
        // Query to check if the user exists
        String doesUserExist = "SELECT * FROM User WHERE Username = ?";
        try (PreparedStatement statement = db.prepareStatement(doesUserExist)) {
            statement.setString(1, user.username);
            ResultSet resultSet =  statement.executeQuery();
            if (resultSet.next()){ // The user exists
                userId = resultSet.getInt("UserId");
                // If so, update info
                String updateQuery = "UPDATE User SET Firstname = ?, Lastname = ?, Password = ? WHERE Username = ?";
                try (PreparedStatement updateStatement = db.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, user.firstname);
                    updateStatement.setString(2, user.lastname);
                    updateStatement.setString(3, password);
                    updateStatement.setString(4, user.username);
                    updateStatement.executeUpdate();
                }
            } else {
                // Query to insert new user
                String addQuery = "INSERT INTO User (Username, Firstname, Lastname, Password) VALUES (?, ?, ?, ?)";
                try (PreparedStatement addStatement = db.prepareStatement(addQuery)) {
                    addStatement.setString(1, user.username);
                    addStatement.setString(2, user.firstname);
                    addStatement.setString(3, user.lastname);
                    addStatement.setString(4, password);
                    addStatement.executeUpdate();
                }
                try (PreparedStatement getUserId = db.prepareStatement(doesUserExist)) {
                    getUserId.setString(1, user.username);
                    // Get the new user id
                    try (ResultSet rs = getUserId.executeQuery()) {
                        if (rs.next()) {
                            System.out.printf("===============NOW============");
                            userId = rs.getInt("UserId");
                        }
                    }
                }
            }
        }
        return userId;
    }

    public boolean doesObjectExist(String tableName, String columnName, Object identifier) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement statement = db.prepareStatement(query)) {
            if (identifier instanceof String) {
                statement.setString(1, (String) identifier);
            } else if (identifier instanceof Integer) {
                statement.setInt(1, (Integer) identifier);
            } else {
                throw new IllegalArgumentException("Unsupported identifier type: " + identifier.getClass().getName());
            }
            return statement.executeQuery().next();
        }
    }

    /**
     * Verify a user's login credentials.
     *
     * @param username
     * @param password
     * @return true if the user exists in the database and the password matches; false otherwise.
     * @throws SQLException
     * <p>
     * Note: this is totally insecure. For real-life password checking, it's important to store only
     * a password hash
     * @see <a href="https://crackstation.net/hashing-security.htm">How to Hash Passwords Properly</a>
     */
    public boolean verifyLogin(String username, String password) throws SQLException {
        String actualPassword = "";
        // Query to check if the user exists
        String doesUserExist = "SELECT * FROM User WHERE Username = ?";
        try (PreparedStatement statement = db.prepareStatement(doesUserExist)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { // The user exists
                actualPassword = resultSet.getString("Password");
                if (actualPassword.equals(password)){
                    return true;
                }

            }
        }
        return false;
    }

    public boolean addObject(String tableName, Object[] values) throws SQLException {
        String[] columnNames;
        boolean ignoreSubmissionId = false;
        if (tableName.equals("User")){
            columnNames = new String[]{"UserId", "Username", "Firstname", "Lastname", "Password"};
        } else if (tableName.equals("Exercise")) {
            columnNames = new String[]{"ExerciseId", "Name", "DueDate"};
        } else if (tableName.equals("Question")) {
            columnNames = new String[]{"ExerciseId", "Name", "Desc", "Points"};
        } else if (tableName.equals("Submission")) {
            if (((Integer) values[0]) != -1){
                columnNames = new String[]{"SubmissionId", "UserId", "ExerciseId", "SubmissionTime"};
            } else {
                Object[] temp = new Object[values.length - 1];
                System.arraycopy(values, 1, temp, 0, values.length - 1);
                values = temp;
                columnNames = new String[]{"UserId", "ExerciseId", "SubmissionTime"};
            }
        } else if (tableName.equals("QuestionGrade")) {
            columnNames = new String[]{"SubmissionId", "QuestionId", "Grade"};
        }else {
            throw new IllegalArgumentException("Unsupported table: " + tableName);
        }
        // Build the query dynamically
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName).append(" (");

        // Add column names
        for (int i = 0; i < columnNames.length; i++) {
            query.append(columnNames[i]);
            if (i < columnNames.length - 1) {
                query.append(", ");
            }
        }

        query.append(") VALUES (");

        // Add placeholders for values
        for (int i = 0; i < values.length; i++) {
            query.append("?");
            if (i < values.length - 1) {
                query.append(", ");
            }
        }

        query.append(")");

        // Prepare the statement and set the values
        try (PreparedStatement statement = db.prepareStatement(query.toString())) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof String) {
                    statement.setString(i + 1, (String) values[i]);
                } else if (values[i] instanceof Integer) {
                    statement.setInt(i + 1, (Integer) values[i]);
                } else if (values[i] instanceof Double) {
                    statement.setDouble(i + 1, (Double) values[i]);
                } else if (values[i] instanceof Long) {
                    statement.setLong(i + 1, (Long) values[i]);
                } else if (values[i] == null) {
                    statement.setNull(i + 1, java.sql.Types.NULL);
                } else {
                    throw new IllegalArgumentException("Unsupported value type: " + values[i].getClass().getName());
                }
            }

            // Execute the insert statement
            return statement.executeUpdate() > 0;
        }
    }


    // =========== Exercise Management =============

    /**
     * Add an exercise to the database.
     *
     * @param exercise
     * @return the new exercise id, or -1 if an exercise with this id already existed in the database.
     * @throws SQLException
     */
    public int addExercise(Exercise exercise) throws SQLException {
        boolean exerciseExist = doesObjectExist("Exercise", "ExerciseId", exercise.id);
        if (!exerciseExist){
            Object[] values = {exercise.id, exercise.name, exercise.dueDate.getTime()};
            boolean added = addObject("Exercise", values);
            if (added){
                for (Question question : exercise.questions){
                    values = new Object[]{exercise.id, question.name, question.desc, question.points};
                    addObject("Question", values);
                }
                return exercise.id;
            }
        }
        return -1;
    }


    /**
     * Return a list of all the exercises in the database.
     * <p>
     * The list should be sorted by exercise id.
     *
     * @return list of all exercises.
     * @throws SQLException
     */
    public List<Exercise> loadExercises() throws SQLException {
        String query = "SELECT * FROM Exercise ORDER BY ExerciseId";
        List<Exercise> exercises = new ArrayList<>();

        try (PreparedStatement statement = db.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                List<Question> questions = new ArrayList<>();
                // Assuming Exercise class has a constructor to map fields
                int id = resultSet.getInt("ExerciseId");
                String name = resultSet.getString("Name");
                Date duration = resultSet.getDate("DueDate"); // Adjust column names/types as needed

                Exercise exercise = new Exercise(id, name, duration);
                questions = loadQuestionsForExercise(exercise);
                exercise.questions = questions;
                exercises.add(exercise);
            }
        }
        return exercises;
    }

    public List<Question> loadQuestionsForExercise(Exercise exercise) throws SQLException {
        String query = "SELECT * FROM Question WHERE ExerciseId = ?";
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement statement = db.prepareStatement(query)){
            statement.setInt(1, exercise.id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()){
                    String name = resultSet.getString("Name");
                    String desc = resultSet.getString("Desc");
                    int points = resultSet.getInt("Points");
                    Exercise.Question question = exercise.new Question(name, desc, points);
                    questions.add(question);
                }
            }

        }
        return questions;
    }

    // ========== Submission Storage ===============

    /**
     * Store a submission in the database.
     * The id field of the submission will be ignored if it is -1.
     * <p>
     * Return -1 if the corresponding user doesn't exist in the database.
     *
     * @param submission
     * @return the submission id.
     * @throws SQLException
     */
    public int storeSubmission(Submission submission) throws SQLException {
        boolean userExist = doesObjectExist("User", "Username", submission.user.username);
        int subId = -1;
        if(userExist) {
            Object[] values = {submission.id, getUserId(submission.user.username), submission.exercise.id, submission.submissionTime.getTime()};
            addObject("Submission", values);
            subId = getSubmissionId(submission.submissionTime.getTime());
        }
        return subId;
    }

    public int getSubmissionId(long time) throws SQLException {
        int subId = -1;
        String subExist = "SELECT * FROM Submission WHERE SubmissionTime = ?";
        try (PreparedStatement statement = db.prepareStatement(subExist)) {
            statement.setLong(1, time);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { // The user exists
                subId = resultSet.getInt("SubmissionId");
            }
        }
        return subId;
    }

    public int getUserId(String username) throws SQLException {
        int userId = -1;
        String doesUserExist = "SELECT * FROM User WHERE Username = ?";
        try (PreparedStatement statement = db.prepareStatement(doesUserExist)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { // The user exists
                userId = resultSet.getInt("UserId");
            }
        }
        return userId;
    }


    // ============= Submission Query ===============


    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the latest submission for the given exercise by the given user.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getLastSubmission(User, Exercise)}
     *
     * @return
     */
    PreparedStatement getLastSubmissionGradesStatement() throws SQLException {
        String sql = "SELECT " +
                "s.SubmissionId, qg.QuestionId, qg.Grade, s.SubmissionTime " +
                "FROM " +
                "Submission s " +
                "INNER JOIN " +
                "QuestionGrade qg " +
                "ON " +
                "s.SubmissionId = qg.SubmissionId " +
                "WHERE " +
                "s.UserId = (SELECT UserId FROM User WHERE Username = ?) " +
                "AND " +
                "s.ExerciseId = ? " +
                "ORDER BY " +
                "s.SubmissionTime " +
                "DESC, " +
                "qg.QuestionId " +
                "LIMIT ?";
        return db.prepareStatement(sql);
    }

    /**
     * Return a prepared SQL statement that, when executed, will
     * return one row for every question of the <i>best</i> submission for the given exercise by the given user.
     * The best submission is the one whose point total is maximal.
     * <p>
     * The rows should be sorted by QuestionId, and each row should contain:
     * - A column named "SubmissionId" with the submission id.
     * - A column named "QuestionId" with the question id,
     * - A column named "Grade" with the grade for that question.
     * - A column named "SubmissionTime" with the time of submission.
     * <p>
     * Parameter 1 of the prepared statement will be set to the User's username, Parameter 2 to the Exercise Id, and
     * Parameter 3 to the number of questions in the given exercise.
     * <p>
     * This will be used by {@link #getBestSubmission(User, Exercise)}
     *
     */
    PreparedStatement getBestSubmissionGradesStatement() throws SQLException {
        // TODO: Implement
        return null;
    }

    /**
     * Return a submission for the given exercise by the given user that satisfies
     * some condition (as defined by an SQL prepared statement).
     * <p>
     * The prepared statement should accept the user name as parameter 1, the exercise id as parameter 2 and a limit on the
     * number of rows returned as parameter 3, and return a row for each question corresponding to the submission, sorted by questionId.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @param stmt
     * @return
     * @throws SQLException
     */
    Submission getSubmission(User user, Exercise exercise, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, user.username);
        stmt.setInt(2, exercise.id);
        stmt.setInt(3, exercise.questions.size());

        ResultSet res = stmt.executeQuery();

        boolean hasNext = res.next();
        if (!hasNext)
            return null;

        int sid = res.getInt("SubmissionId");
        Date submissionTime = new Date(res.getLong("SubmissionTime"));

        float[] grades = new float[exercise.questions.size()];

        for (int i = 0; hasNext; ++i, hasNext = res.next()) {
            grades[i] = res.getFloat("Grade");
        }

        return new Submission(sid, user, exercise, submissionTime, (float[]) grades);
    }

    /**
     * Return the latest submission for the given exercise by the given user.
     * <p>
     * Return null if the user has not submitted the exercise (or is not in the database).
     *
     * @param user
     * @param exercise
     * @return
     * @throws SQLException
     */
    public Submission getLastSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getLastSubmissionGradesStatement());
    }


    /**
     * Return the submission with the highest total grade
     *
     * @param user the user for which we retrieve the best submission
     * @param exercise the exercise for which we retrieve the best submission
     * @return
     * @throws SQLException
     */
    public Submission getBestSubmission(User user, Exercise exercise) throws SQLException {
        return getSubmission(user, exercise, getBestSubmissionGradesStatement());
    }
}
