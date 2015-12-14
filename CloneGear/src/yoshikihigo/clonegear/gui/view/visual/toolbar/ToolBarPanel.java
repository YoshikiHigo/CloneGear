package yoshikihigo.clonegear.gui.view.visual.toolbar;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.visual.VisualAnalysisView;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;


public class ToolBarPanel extends JPanel implements VisualViewInterface, Observer {

    public ToolBarPanel(final VisualAnalysisView parentContainer) {

        super(new FlowLayout(FlowLayout.LEFT));

        // �t�@�C���Z���N�V�����p�l���\���E�B���p
        {
            final JPanel fileSelectionPanelShowHide = new JPanel(new FlowLayout(FlowLayout.LEFT));
            final JLabel name = new JLabel("File Selection Panel");
            this.fileSelectionPanelShowButton = new JRadioButton("show", true);
            this.fileSelectionPanelHideButton = new JRadioButton("hide", false);
            final ButtonGroup fileSelectionButtonGroup = new ButtonGroup();
            fileSelectionButtonGroup.add(this.fileSelectionPanelShowButton);
            fileSelectionButtonGroup.add(this.fileSelectionPanelHideButton);
            fileSelectionPanelShowHide.add(name);
            fileSelectionPanelShowHide.add(this.fileSelectionPanelShowButton);
            fileSelectionPanelShowHide.add(this.fileSelectionPanelHideButton);
            fileSelectionPanelShowHide.setBorder(new LineBorder(Color.black));
            this.add(fileSelectionPanelShowHide);

            this.fileSelectionPanelShowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (fileSelectionPanelShowButton.isSelected()) {
                        parentContainer.showFileSelectionPanel();
                    }
                }
            });

            this.fileSelectionPanelHideButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (fileSelectionPanelHideButton.isSelected()) {
                        parentContainer.hideFileSelectionPanel();
                    }
                }
            });
        }

        // �N���[���y�A���X�g�\���E�B���p
        {
            final JPanel clonePairListShowHide = new JPanel(new FlowLayout(FlowLayout.LEFT));
            final JLabel name = new JLabel("Clone Pair List");
            this.clonePairListLShowButton = new JRadioButton("show", true);
            this.clonePairListHideButton = new JRadioButton("hide", false);
            final ButtonGroup clonePairListButtonGroup = new ButtonGroup();
            clonePairListButtonGroup.add(this.clonePairListLShowButton);
            clonePairListButtonGroup.add(this.clonePairListHideButton);
            clonePairListShowHide.add(name);
            clonePairListShowHide.add(this.clonePairListLShowButton);
            clonePairListShowHide.add(this.clonePairListHideButton);
            clonePairListShowHide.setBorder(new LineBorder(Color.black));
            this.add(clonePairListShowHide);

            this.clonePairListLShowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (clonePairListLShowButton.isSelected()) {
                        parentContainer.showClonePairList();
                    }
                }
            });

            this.clonePairListHideButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (clonePairListHideButton.isSelected()) {
                        parentContainer.hideClonePairList();
                    }
                }
            });
        }

        // �\�[�X�R�[�h�r���[�����p
        {
            final JPanel sourceCodeSplitDirection = new JPanel(new FlowLayout(FlowLayout.LEFT));
            final JLabel name = new JLabel("Source Code View");
            this.sourceCodeVerticalSplitButton = new JRadioButton("vertical", true);
            this.sourceCodeHorizontalSplitButton = new JRadioButton("horizontal", false);
            final ButtonGroup sourceCodeButtonGroup = new ButtonGroup();
            sourceCodeButtonGroup.add(this.sourceCodeHorizontalSplitButton);
            sourceCodeButtonGroup.add(this.sourceCodeVerticalSplitButton);
            sourceCodeSplitDirection.add(name);
            sourceCodeSplitDirection.add(this.sourceCodeHorizontalSplitButton);
            sourceCodeSplitDirection.add(this.sourceCodeVerticalSplitButton);
            sourceCodeSplitDirection.setBorder(new LineBorder(Color.black));
            this.add(sourceCodeSplitDirection);

            this.sourceCodeHorizontalSplitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (sourceCodeHorizontalSplitButton.isSelected()) {
                        parentContainer.setHorizontalSplitSourceCodeView();
                    }
                }
            });

            this.sourceCodeVerticalSplitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (sourceCodeVerticalSplitButton.isSelected()) {
                        parentContainer.setVerticalSplitSourceCodeView();
                    }
                }
            });
        }

        // �s�K�v�N���[���̕\���E��\��
        {
            final JPanel uninterestingClonesShowNot = new JPanel(new FlowLayout(FlowLayout.LEFT));
            final JLabel name = new JLabel("Uninteresting Clones");
            this.uninterestingClonesShowButton = new JRadioButton("show", true);
            this.uninterestingClonesHideButton = new JRadioButton("not", false);
            final ButtonGroup uninterestingClonesButtonGroup = new ButtonGroup();
            uninterestingClonesButtonGroup.add(this.uninterestingClonesShowButton);
            uninterestingClonesButtonGroup.add(this.uninterestingClonesHideButton);
            uninterestingClonesShowNot.add(name);
            uninterestingClonesShowNot.add(this.uninterestingClonesShowButton);
            uninterestingClonesShowNot.add(this.uninterestingClonesHideButton);
            uninterestingClonesShowNot.setBorder(new LineBorder(Color.black));
            this.add(uninterestingClonesShowNot);

            this.uninterestingClonesShowButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (uninterestingClonesShowButton.isSelected()) {
                        UninterestingClonesDisplay.getInstance(UNINTERESTING).set(true);
                    }
                }
            });

            this.uninterestingClonesHideButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (uninterestingClonesHideButton.isSelected()) {
                        UninterestingClonesDisplay.getInstance(UNINTERESTING).set(false);
                    }
                }
            });

            // ���Z�b�g�{�^���p
            {
                final JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                final JButton resetButton = new JButton("Reset");
                resetPanel.add(resetButton);
                resetPanel.setBorder(new LineBorder(Color.black));
                this.add(resetPanel);

                resetButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        parentContainer.reset();
                    }
                });
            }
        }
    }

    public void reset() {
        this.fileSelectionPanelShowButton.doClick();
        this.clonePairListLShowButton.doClick();
        this.sourceCodeVerticalSplitButton.doClick();
        this.uninterestingClonesShowButton.doClick();
    }

    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
    }

    private final JRadioButton fileSelectionPanelShowButton;

    private final JRadioButton fileSelectionPanelHideButton;

    private final JRadioButton clonePairListLShowButton;

    private final JRadioButton clonePairListHideButton;

    private final JRadioButton sourceCodeVerticalSplitButton;

    private final JRadioButton sourceCodeHorizontalSplitButton;

    private final JRadioButton uninterestingClonesShowButton;

    private final JRadioButton uninterestingClonesHideButton;
}
