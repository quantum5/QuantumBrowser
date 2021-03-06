package quantum.browser.ui;

import org.cef.CefApp;
import org.cef.CefSettings;
import quantum.browser.data.Settings;
import quantum.browser.handler.AppHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main browser frame.
 */
public class MainFrame extends JFrame {
    final CefApp app;
    final ToolBar toolBar;
    final MenuBar menubar;
    final TabManager tabManager;
    final StatusBar statusBar;

    /**
     * Constructor.
     * @param osrEnabled off-screen rendering?
     */
    public MainFrame(boolean osrEnabled) {
        // Create CEF app.
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = osrEnabled;
        settings.cache_path = Settings.cacheDirectory.getAbsolutePath();
        settings.log_file = Settings.logFile.getAbsolutePath();
        settings.background_color = settings.new ColorType(255, 255, 255, 255);

        app = CefApp.getInstance(settings);
        CefApp.addAppHandler(new AppHandler());

        // Print version.
        System.out.println(app.getVersion());

        // Setup components and window.
        statusBar = new StatusBar();
        tabManager = new TabManager(this, osrEnabled);
        menubar = new MenuBar(this);
        toolBar = new ToolBar(this);
        setJMenuBar(menubar);
        getContentPane().add(toolBar, BorderLayout.NORTH);
        getContentPane().add(tabManager, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        pack();
        // Set window size as remembered.
        setSize(Settings.getDimension("window_size", new Dimension(800, 600)));

        // Handle window close.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Remember window size.
                Settings.putDimension("window_size", getSize());
                CefApp.getInstance().dispose();
                dispose();
            }
        });

        // Create the first tab.
        tabManager.newTab();

        // Key stroke bindings for keyboard shortcuts.
        new Object() {{
            getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('T', Event.CTRL_MASK), this);
            getRootPane().getActionMap().put(this, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tabManager.newTab();
                }
            });
        }};
        new Object() {{
            getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F5"), this);
            getRootPane().getActionMap().put(this, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tabManager.currentTab().browser.reload();
                }
            });
        }};
        new Object() {{
            getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, Event.CTRL_MASK), this);
            getRootPane().getActionMap().put(this, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tabManager.currentTab().browser.reloadIgnoreCache();
                }
            });
        }};
        new Object() {{
            getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("F6"), this);
            getRootPane().getActionMap().put(this, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toolBar.addressBar.requestFocusInWindow();
                }
            });
        }};
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title + " - Quantum Browser");
    }
}
