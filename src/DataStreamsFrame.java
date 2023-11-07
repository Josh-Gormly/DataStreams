import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashSet;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamsFrame extends JFrame
{
    JPanel mainPnl;
    JPanel buttonPnl;
    JPanel displayPnl;
    JPanel searchPnl;
    JButton loadBtn;
    JButton filterBtn;
    JButton quitBtn;
    JLabel searchLabel;
    JTextArea lArea;
    JTextArea rArea;
    JTextField searchStringText;
    JScrollPane lPane;
    JScrollPane rPane;
    private File selectedFile;
    private Path filePath;
    private Set sets = new HashSet();

    DataStreamsFrame()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createButtonPnl();
        createDisplayPnl();
        createSearchPnl();

        mainPnl.add(searchPnl, BorderLayout.NORTH);
        mainPnl.add(buttonPnl, BorderLayout.SOUTH);
        mainPnl.add(displayPnl, BorderLayout.CENTER);

        add(mainPnl);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void createButtonPnl()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1,3));
        buttonPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        loadBtn = new JButton("Load");
        filterBtn = new JButton("Filer");
        quitBtn = new JButton("Quit");
        loadBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        filterBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        quitBtn.setFont(new Font("Times New Roman", Font.BOLD, 20));
        filterBtn.setEnabled(false);
        filterBtn.setBackground(new Color(240,240,240));

        loadBtn.addActionListener((ActionEvent e) -> load());
        filterBtn.addActionListener((ActionEvent e) -> filter());
        quitBtn.addActionListener((ActionEvent e) ->
        {
            int res = JOptionPane.showOptionDialog(null,"Are you wanting to quit?", "Message", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null, new Object[]{"Yes", "No"}, JOptionPane.YES_OPTION);
            if(res == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
            else if(res == JOptionPane.NO_OPTION)
            {
                JOptionPane.showMessageDialog(null, "Canceled quit request", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
            else if(res == JOptionPane.CLOSED_OPTION)
            {
                JOptionPane.showMessageDialog(null,"Canceled quit request", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPnl.add(loadBtn);
        buttonPnl.add(filterBtn);
        buttonPnl.add(quitBtn);
    }
    public void createDisplayPnl()
    {
        displayPnl = new JPanel();
        displayPnl.setLayout(new GridLayout(1,2));
        displayPnl.setBorder(new TitledBorder(new EtchedBorder(), ""));

        lArea = new JTextArea();
        lArea.setEditable(false);
        lArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        lPane = new JScrollPane(lArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        lArea.setBorder(new TitledBorder("Original File"));

        rArea = new JTextArea();
        rArea.setEditable(false);
        rArea.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        rPane = new JScrollPane(rArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        displayPnl.add(lPane);
        displayPnl.add(rPane);
    }
    public void createSearchPnl()
    {
        searchPnl = new JPanel();
        searchPnl.setLayout(new GridLayout(1,2));
        searchStringText = new JTextField();
        searchLabel = new JLabel("Enter Your Search String: ");
        searchLabel.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        searchLabel.setHorizontalAlignment(JLabel.CENTER);

        searchPnl.add(searchLabel);
        searchPnl.add(searchStringText);
    }
    public void load()
    {
        JFileChooser chooser = new JFileChooser();
        File workingDirectory = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(workingDirectory);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
            filePath = selectedFile.toPath();
        }
        filterBtn.setEnabled(true);
        filterBtn.setBackground(null);
        JOptionPane.showMessageDialog(mainPnl, "File Loaded", "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    public void filter()
    {
        lArea.setText(" ");
        rArea.setText(" ");
        String wFilter = searchStringText.getText();
        String rec;
        try (Stream<String> lines = Files.lines(Paths.get(selectedFile.getPath())))
        {
            Set<String> sets = lines.filter(w -> w.contains(wFilter)).collect(Collectors.toSet());
            sets.forEach(w -> rArea.append(w + "\n"));
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!!!");
            e.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        try
        {
            InputStream input = new BufferedInputStream(Files.newInputStream(filePath, CREATE));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            int line = 0;
            while(reader.ready())
            {
                rec = reader.readLine();
                lArea.append(rec + "\n");
                line++;
            }
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("File not found!!!");
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}