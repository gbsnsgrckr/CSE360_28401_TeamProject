package tests;

import application.Message;
import databasePart1.DatabaseHelper;
import org.junit.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * ReportTestingAutomation is a JUnit 4 test class used to validate 
 * the Create, Read, Update, and Delete (CRUD) operations on report-type messages
 * in the messaging system. It ensures the report functionality behaves correctly.
 * 
 * <p>Test Structure:
 * <ul>
 *     <li><b>Test 1</b>: Create four report messages</li>
 *     <li><b>Test 2</b>: Read and print those reports</li>
 *     <li><b>Test 3</b>: Edit their subject and body</li>
 *     <li><b>Test 4</b>: Display updated reports</li>
 *     <li><b>Test 5</b>: Delete all report messages</li>
 *     <li><b>Test 6</b>: Confirm the database no longer contains any reports</li>
 * </ul>
 *
 * @author Chris
 * @version 1.0
 * @since 2025-04-11
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReportTestingAutomation {

    private static DatabaseHelper db;

    /**
     * Default constructor.
     */
    public ReportTestingAutomation() {
        // Default constructor
    }

    /**
     * Sets up the database connection before all tests.
     */
    @BeforeClass
    public static void setup() {
        db = new DatabaseHelper();
        try {
            db.connectToDatabase();
        } catch (SQLException e) {
            Assert.fail("Failed to connect to database.");
        }
    }

    /**
     * Deletes all existing reports before tests run to ensure a clean state.
     *
     * @throws SQLException if database cleanup fails
     */
    private void clearExistingReports() throws SQLException {
        List<Message> existing = db.qaHelper.retrieveReportedObjects();
        for (Message r : existing) {
            db.qaHelper.deleteMessage(r.getMessageID());
        }
        System.out.println("[Setup] Cleared " + existing.size() + " existing reports.");
    }

    /**
     * [Test 1] Create four sample reports.
     *
     * @throws SQLException if message creation fails
     */
    @Test public void test1_createReports() throws SQLException {
        clearExistingReports();

        Message r1 = new Message(1, "q", 1337, 2, "Report1", "Issue with Q1", true);
        Message r2 = new Message(2, "a", 1337, 2, "Report2", "Issue with A1", true);
        Message r3 = new Message(3, "r", 1337, 2, "Report3", "Issue with R1", true);
        Message r4 = new Message(4, "m", 1337, 2, "Report4", "Issue with M1", true);

        db.qaHelper.createMessage(r1, true);
        db.qaHelper.createMessage(r2, true);
        db.qaHelper.createMessage(r3, true);
        db.qaHelper.createMessage(r4, true);

        System.out.println("[Test 1] Four test reports created.");
    }

    /**
     * [Test 2] Display all reports in console.
     *
     * @throws SQLException if message retrieval fails
     */
    @Test public void test2_readReports() throws SQLException {
        List<Message> reports = db.qaHelper.retrieveReportedObjects();
        System.out.println("[Test 2] Displaying all reports:");
        for (Message m : reports) {
            System.out.println("ID=" + m.getMessageID() + ", Subject=" + m.getSubject() + ", Msg=" + m.getMessage());
        }
        Assert.assertTrue(reports.size() >= 4);
    }

    /**
     * [Test 3] Update contents of each report using QAHelper1's updateReport().
     *
     * @throws SQLException if update fails
     */
    @Test
    public void test3_updateReports() throws SQLException {
        List<Message> reports = db.qaHelper.retrieveReportedObjects();

        for (Message m : reports) {
            String updatedSubject = "Updated " + m.getSubject();
            String updatedMessage = "Updated content: " + m.getMessage();

            db.qaHelper.updateReport(m.getMessageID(), updatedSubject, updatedMessage);
        }

        System.out.println("[Test 3] All report messages updated using helper method.");
    }

    /**
     * [Test 4] Display all report messages in console.
     *
     * @throws SQLException if retrieval fails
     */
    @Test public void test4_readReports() throws SQLException {
        List<Message> reports = db.qaHelper.retrieveReportedObjects();
        System.out.println("[Test 4] Displaying all reports:");
        for (Message m : reports) {
            System.out.println("ID=" + m.getMessageID() + ", Subject=" + m.getSubject() + ", Msg=" + m.getMessage());
        }
        Assert.assertTrue(reports.size() >= 4);
    }

    /**
     * [Test 5] Delete the four test reports.
     *
     * @throws SQLException if deletion fails
     */
    @Test public void test5_deleteReports() throws SQLException {
        List<Message> reports = db.qaHelper.retrieveReportedObjects();
        for (Message m : reports) {
            db.qaHelper.deleteMessage(m.getMessageID());
        }
        System.out.println("[Test 5] Test reports deleted.");
    }

    /**
     * [Test 6] Confirm report table is empty.
     *
     * @throws SQLException if retrieval fails
     */
    @Test public void test6_confirmReportsDeleted() throws SQLException {
        List<Message> reports = db.qaHelper.retrieveReportedObjects();
        System.out.println("[Test 6] Remaining reports: " + reports.size());
        Assert.assertEquals(0, reports.size());
    }
}
