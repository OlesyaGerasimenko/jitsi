/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.plugin.branding;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

public class WelcomeWindow extends JDialog
{
    private WindowBackground mainPanel = new WindowBackground();

    private JLabel titleLabel
        = new JLabel(BrandingActivator.getResources()
                .getSettingsString("applicationName"));

    private JLabel versionLabel = new JLabel(" "
            + System.getProperty("sip-communicator.version"));

    private JTextArea logoArea = new JTextArea(
        BrandingActivator.getResources().getI18NString("logoMessage"));

    private StyledHTMLEditorPane rightsArea = new StyledHTMLEditorPane();

    private StyledHTMLEditorPane licenseArea = new StyledHTMLEditorPane();

    private JPanel textPanel = new JPanel();

    private JPanel loadingPanel = new JPanel(new BorderLayout());

    private JLabel loadingLabel = new JLabel(
        BrandingActivator.getResources().getI18NString("loading") + ": ");

    private JLabel bundleLabel = new JLabel();

    public WelcomeWindow()
    {
        this.setTitle(
            BrandingActivator.getResources().getSettingsString("applicationName"));

        this.setModal(false);
        this.setUndecorated(true);

        this.mainPanel.setLayout(new BorderLayout());

        this.textPanel.setPreferredSize(new Dimension(470, 280));
        this.textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        this.textPanel
                .setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        this.textPanel.setOpaque(false);

        this.titleLabel.setFont(Constants.FONT.deriveFont(Font.BOLD, 28));
        this.titleLabel.setForeground(Constants.TITLE_COLOR);
        this.titleLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        this.versionLabel.setFont(Constants.FONT.deriveFont(Font.BOLD, 18));
        this.versionLabel.setForeground(Constants.TITLE_COLOR);
        this.versionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        int logoAreaFontSize = BrandingActivator.getResources().
            getSettingsInt("aboutLogoFontSize");

        this.logoArea.setFont(
            Constants.FONT.deriveFont(Font.BOLD, logoAreaFontSize));
        this.logoArea.setForeground(Constants.TITLE_COLOR);
        this.logoArea.setOpaque(false);
        this.logoArea.setLineWrap(true);
        this.logoArea.setWrapStyleWord(true);
        this.logoArea.setEditable(false);
        this.logoArea.setPreferredSize(new Dimension(100, 20));
        this.logoArea.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.logoArea.setBorder(BorderFactory.createEmptyBorder(20, 190, 0, 0));

        this.rightsArea.setContentType("text/html");
        this.rightsArea.appendToEnd(BrandingActivator.getResources().getI18NString(
            "welcomeMessage",
            new String[]{
                Constants.TEXT_COLOR,
                BrandingActivator.getResources().getSettingsString("applicationName"),
                BrandingActivator.getResources().getSettingsString("applicationWebSite")
                }));

        this.rightsArea.setPreferredSize(new Dimension(50, 50));
        this.rightsArea
                .setBorder(BorderFactory.createEmptyBorder(0, 190, 0, 0));
        this.rightsArea.setOpaque(false);
        this.rightsArea.setEditable(false);
        this.rightsArea.setAlignmentX(Component.RIGHT_ALIGNMENT);

        this.licenseArea.setContentType("text/html");
        this.licenseArea.appendToEnd(BrandingActivator.getResources().getI18NString(
            "license",
            new String[]
                       {
                            Constants.TEXT_COLOR
                       }));

        this.licenseArea.setPreferredSize(new Dimension(50, 20));
        this.licenseArea.setBorder(BorderFactory
                .createEmptyBorder(0, 190, 0, 0));
        this.licenseArea.setOpaque(false);
        this.licenseArea.setEditable(false);
        this.licenseArea.setAlignmentX(Component.RIGHT_ALIGNMENT);

        this.bundleLabel.setFont(loadingLabel.getFont().deriveFont(Font.PLAIN));
        this.loadingPanel.setOpaque(false);
        this.loadingPanel.add(loadingLabel, BorderLayout.WEST);
        this.loadingPanel.add(bundleLabel, BorderLayout.CENTER);
        this.loadingPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        this.loadingPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.textPanel.add(titleLabel);
        this.textPanel.add(versionLabel);
        this.textPanel.add(logoArea);
        this.textPanel.add(rightsArea);
        this.textPanel.add(licenseArea);

        this.mainPanel.add(textPanel, BorderLayout.CENTER);
        this.mainPanel.add(loadingPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);

        this.setResizable(false);

        this.mainPanel.setPreferredSize(new Dimension(570, 330));

        this.setLocation(
            Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 527 / 2,
            Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 305 / 2);

        // Close the splash screen on simple click or Esc.
        this.getGlassPane().addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                WelcomeWindow.this.close();
            }
        });

        this.getGlassPane().setVisible(true);

        ActionMap amap = this.getRootPane().getActionMap();

        amap.put("close", new CloseAction());

        InputMap imap = this.getRootPane().getInputMap(
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    }

    protected void close()
    {
        this.dispose();
    }

    public void setBundle(String bundleName)
    {
        this.bundleLabel.setText(bundleName);

        this.loadingPanel.revalidate();
        this.loadingPanel.repaint();
    }

    /**
     * The action invoked when user presses Escape key.
     */
    private class CloseAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            WelcomeWindow.this.close();
        }
    }

    /**
     * Constructs the window background in order to have a background image.
     */
    private class WindowBackground
        extends JPanel
    {
        private BufferedImage cache;

        private int cacheHeight;

        private int cacheWidth;

        private final Image image;

        public WindowBackground()
        {
            setOpaque(true);

            Image image = null;
            try
            {
                image =
                    ImageIO.read(BrandingActivator.getResources().getImageURL(
                        "splashScreenBg"));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            this.image = image;

            if (image != null)
            {
                setPreferredSize(new Dimension(image.getWidth(this), image
                    .getHeight(this)));
            }
        }

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            /*
             * Drawing an Image with a data layout and color model compatible
             * with this JPanel is magnitudes faster so create and use such an
             * Image from the original drawn by this instance.
             */
            int width = getWidth();
            int height = getHeight();
            boolean imageIsChanging = false;
            if ((cache == null) || (cacheWidth != width)
                || (cacheHeight != height))
            {
                cache =
                    g2.getDeviceConfiguration().createCompatibleImage(width,
                        height);
                cacheWidth = width;
                cacheHeight = height;

                Graphics2D cacheGraphics = cache.createGraphics();
                try
                {
                    super.paintComponent(cacheGraphics);

                    cacheGraphics.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                    imageIsChanging =
                        !cacheGraphics.drawImage(image, 0, 0, null);

                    cacheGraphics.setColor(new Color(150, 150, 150));
                    cacheGraphics.drawRoundRect(0, 0, width - 1, height - 1, 5,
                        5);
                }
                finally
                {
                    cacheGraphics.dispose();
                }
            }

            g2.drawImage(cache, 0, 0, null);

            /*
             * Once the original Image drawn by this instance has been fully
             * loaded, we're free to use its "compatible" caching representation
             * for the purposes of optimized execution speed.
             */
            if (imageIsChanging)
            {
                cache = null;
            }
        }
    }
}
