package dynamic.type.inferences.windowOnStartUp;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import dynamic.type.inferences.model.translator.RanksGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.lang.documentation.DocumentationMarkup.*;

@State(
        name = "Show VaDima message dialog at PyCharm start",
        storages = {@Storage("VaDimaPlugin.xml")}
)
public class WindowExistingClasses extends DialogWrapper implements PersistentStateComponent<WindowExistingClasses.State> {
    private State myState = new State();

    private static final String HTML_NEW_LINE = "<br>";
    private static final String HTML_OPEN_TAG = "<html>";
    private static final String HTML_CLOSE_TAG = "</html>";

    public WindowExistingClasses(Project project) {
        super(project);
        init();
        setTitle("VaDima Plugin Information");
    }

    @Override
    @Nullable
    protected JComponent createCenterPanel() {
        if (!myState.getSavedNeverShow()) {
            JPanel jPanel = new JPanel(new BorderLayout());
            RanksGetter ranksGetter = new RanksGetter();
            List<String> ranks = ranksGetter.getRanksFromFile();
            JBList<String> jbList = new JBList<>(ranks);
            JBScrollPane scrollPane = new JBScrollPane(jbList);

            scrollPane.createVerticalScrollBar();
            JLabel jLabel = new JLabel(
                    HTML_OPEN_TAG
                            .concat(DEFINITION_START)
                            .concat("Thanks for downloading VaDima plugin!")
                            .concat(DEFINITION_END)
                            .concat(HTML_NEW_LINE)
                            .concat(SECTIONS_START)
                            .concat("VaDima can recognize next variable types:")
                            .concat(HTML_CLOSE_TAG)
            );

            JBCheckBox checkBox = new JBCheckBox("Never show again");
            jPanel.add(jLabel, BorderLayout.NORTH);
            jPanel.add(scrollPane, BorderLayout.CENTER);
            jPanel.add(checkBox, BorderLayout.AFTER_LAST_LINE);
            checkBox.addChangeListener(e -> {
                myState.setSavedNeverShow(checkBox.isSelected());
            });
            return jPanel;
        } else
            return null;
    }


    @Override
    public @Nullable WindowExistingClasses.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

    public static class State {
        private boolean savedNeverShow = false;

        public boolean getSavedNeverShow() {
            return savedNeverShow;
        }

        public void setSavedNeverShow(boolean savedNeverShow) {
            this.savedNeverShow = savedNeverShow;
        }
    }
}
