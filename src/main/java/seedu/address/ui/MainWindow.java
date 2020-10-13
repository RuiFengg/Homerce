package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.ui.appointmentpanel.AppointmentListPanel;
import seedu.address.ui.clientpanel.ClientListPanel;
import seedu.address.ui.revenuepanel.RevenueListPanel;
import seedu.address.ui.servicepanel.ServiceListPanel;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    // Panels for each component
    private ServiceListPanel serviceListPanel;
    private ClientListPanel clientListPanel;
    private AppointmentListPanel appointmentListPanel;
    private RevenueListPanel revenueListPanel;
    // private ExpenseListPanel expenseListPanel;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    @FXML
    private StackPane sideTabsBarPlaceholder;

    @FXML
    private StackPane tabPanelPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        //setAccelerators();

        helpWindow = new HelpWindow();

        // Set up list panels
        clientListPanel = new ClientListPanel(logic.getFilteredClientList());
        serviceListPanel = new ServiceListPanel(logic.getFilteredServiceList());
        appointmentListPanel = new AppointmentListPanel(logic.getFilteredAppointmentList());
        revenueListPanel = new RevenueListPanel(logic.getFilteredRevenueList());
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        serviceListPanel = new ServiceListPanel(logic.getFilteredServiceList());

        clientListPanel = new ClientListPanel(logic.getFilteredClientList());

        revenueListPanel = new RevenueListPanel(logic.getFilteredRevenueList());

        //expenseListPanel = new ExpenseListPanel(logic.getFilteredExpenseList());

        appointmentListPanel = new AppointmentListPanel(logic.getFilteredAppointmentList());

        // Default view for user on app startup
        switchTab(ClientListPanel.TAB_NAME);
        sideTabsBarPlaceholder.getChildren().add(new SideTabsBar(this::switchTab).getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Switches tab to the specified tab name.
     */
    private void switchTab(String tabName) {
        tabPanelPlaceholder.getChildren().clear();
        statusbarPlaceholder.getChildren().clear();
        statusbarPlaceholder.getChildren().add(new StatusBarFooter(tabName).getRoot());

        switch (tabName) {
        case ClientListPanel.TAB_NAME:
            tabPanelPlaceholder.getChildren().add(clientListPanel.getRoot());
            break;
        case ServiceListPanel.TAB_NAME:
            tabPanelPlaceholder.getChildren().add(serviceListPanel.getRoot());
            break;
        case AppointmentListPanel.TAB_NAME:
            tabPanelPlaceholder.getChildren().add(appointmentListPanel.getRoot());
            break;
        case RevenueListPanel.TAB_NAME:
            tabPanelPlaceholder.getChildren().add(revenueListPanel.getRoot());
            break;
        default:
            throw new AssertionError("No such tab name: " + tabName);
        }
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }


    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("Invalid command: " + commandText);
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
