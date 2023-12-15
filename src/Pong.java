import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * The Pong class represents the main class for the Pong game.
 * It extends JFrame and includes the game logic and GUI components.
 *
 * @author Tales & Jake
 */

public class Pong extends JFrame {  
    
    protected JButton newGame_Button;
    protected JButton exit_Button;
    protected JButton difficulty_Button;
    protected JButton customize_Button;
    protected JButton back_Button;
    private volatile boolean running = true;
    private int rectangle1_Y = 390;
    private int rectangle2_Y = 390;
    private static final int BALL_SIZE = 10;
    private boolean threadResuming = false;
    private int ball_X = 600; private int ball_Y = 400;
    private final int finalball_X = 600; private final int finalball_Y = 400;
    private Color currentColor = new Color(255, 255, 255);
    private boolean buttonsVisible = true;
    ColorPaletteApp colorPaletteApp = new ColorPaletteApp();
    ArrayList <Color> colors = new ArrayList<>();
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean stop_ball = true;
    private boolean downPressed = false;
    private boolean changeBall = false;
    private int ballDirectionX = 1; 
    private int ballDirectionY = 1;
    private int difficult_ = 7;
    private int sleep_thread = 1000;
    private int diff_choice = 0;
    private int P1_score = 0; private int P2_score = 0;
    public JPanel panel_title = new JPanel();
    public JLabel title = new JLabel("PONG");
    public JPanel buttonContainer = new JPanel(new GridBagLayout());
    public JPanel p_back = new JPanel();
    public JLabel score = new JLabel(P1_score+"                       "+P2_score);
    public JPanel win = new JPanel();
    public JLabel win_P = new JLabel(" ");
    public JPanel Record = new JPanel();
    public JLabel R_score= new JLabel("");

    /**
     * Inner class RettangoliMover represents a thread responsible for moving rectangles and the ball.
     * It implements the Runnable interface.
     */
    public class RettangoliMover implements Runnable {
        Thread t;
        boolean suspendThread;

        RettangoliMover() {
            t = new Thread(this);
            suspendThread = false;
        }

        @Override
            public void run() {
                synchronized (this){
                    while(suspendThread){
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                while (running) {

                    if (stop_ball) {
                        
                        //Crea un nuovo thread per il ritardo
                            Thread delayThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ball_X = 600;
                                    ball_Y = 400;
                                    try {
                                        Thread.sleep(sleep_thread); // Introduci un ritardo di 2000 millisecondi (2 secondi)
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        
                                    }
                                    
                                    // Ripristina il gioco
                                    stop_ball = false;
                                }
                            });
                            
                            // Avvia il thread di ritardo
                            delayThread.start();
                        }
                    // Gestisci l'input per il primo rettangolo
                    if (wPressed) {
                        rectangle1_Y -= 5;
                    }
                    if (sPressed) {
                        rectangle1_Y += 5;
                    }
    
                    // Gestisci l'input per il secondo rettangolo
                    if (upPressed) {
                        rectangle2_Y -= 5;
                    }
                    if (downPressed) {
                        rectangle2_Y += 5;
                    }

                   if(!changeBall) {
                        ball_X += difficult_ * ballDirectionX; // Modifica la velocità del cerchio
                        ball_Y += difficult_ * ballDirectionY; // Modifica la velocità del cerchio
                   }
                    
                    // Gestisci collisioni del cerchio con i bordi
                        if (ball_X <= 0) {
                            stop_ball = true;
                            ball_X = 600;
                            ball_Y = 400;
                            P2_score++;
                            score.setText(P1_score+"                       "+P2_score);
                        }
                        if (ball_X >= getWidth() - 20){
                            ball_X = 600;
                            ball_Y = 400;
                            stop_ball = true;
                            P1_score++;
                            score.setText(P1_score+"                       "+P2_score);
                        }
                        if (P1_score == 10 || P2_score == 10) {
                            win.setVisible(true);
                            buttonsVisible = true;
                            changeBall = true;
                            win_P.setFont(new Font("Typeface Mario World Pixel Filled", Font.BOLD, 100));
                            if (P1_score == 10) {
                                win_P.setText("P1 win");
                            } else {
                                win_P.setText("P2 win");
                            }
                            P1_score = 0; P2_score = 0;
                            try {
                            Thread.sleep(2000); // Aggiorna la posizione ogni 10 millisecondi
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            win.setVisible(false);
                            buttonContainer.setVisible(true);
                            back_Button.setVisible(false);
                            title.setVisible(true);
                            score.setVisible(false);
                            P1_score = 0; P2_score = 0;
                        }
                    if (ball_Y <= 0 || ball_Y >= getHeight() - 20) {
                        ballDirectionY = -ballDirectionY;
                    }
                    if (ball_X < 80 && ball_Y >= rectangle1_Y && ball_Y <= rectangle1_Y + 120) {
                        // La palla colpisce il rettangolo sinistro
                        ballDirectionX = 1;
                    } else if (ball_X > 1110 && ball_Y >= rectangle2_Y && ball_Y <= rectangle2_Y + 120) {
                        // La palla colpisce il rettangolo destro
                        ballDirectionX = -1;
                    }

    
                    repaint();
    
                    try {
                        Thread.sleep(10); // Aggiorna la posizione ogni 10 millisecondi
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized void suspendThread(){
                suspendThread = true;
            }
            synchronized void resumeThread(){
                suspendThread = false;
                notify();
            }

        }

    /**
     * Overrides the paint method of JFrame to draw game components.
     * 
     * @param g The Graphics object used for drawing.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!buttonsVisible) {
            g.setColor(currentColor);
            if (rectangle1_Y < 0) {
                rectangle1_Y = 0;
            } else if (rectangle1_Y > getHeight() - 120) {
                rectangle1_Y = getHeight() - 120;
            }
    
            if (rectangle2_Y < 0) {
                rectangle2_Y = 0;
            } else if (rectangle2_Y > getHeight() - 120) {
                rectangle2_Y = getHeight() - 120;
            }
            g.fillRoundRect(50, rectangle1_Y, 30, 80, 30, 30);
            g.fillRoundRect(1110, rectangle2_Y, 30, 80, 30, 30);
            if(!changeBall) {
                g.fillOval(ball_X, ball_Y, BALL_SIZE, BALL_SIZE);
            } else {
                g.fillOval(finalball_X, finalball_Y, BALL_SIZE, BALL_SIZE);
            }
        }
    }
    
    RettangoliMover rettangoliMover = new RettangoliMover();
    Thread moveRett = new Thread(rettangoliMover);

    /**
     * The Pong constructor initializes the game, sets up the GUI components,
     * and defines event listeners.
     *
     * @throws FileNotFoundException if a specified file is not found.
     * @throws UnsupportedEncodingException if the encoding is not supported.
     */
    public Pong () throws FileNotFoundException, UnsupportedEncodingException {
        
        win.setSize(1200, 800);
        win.setBackground(Color.GRAY);
        win.setVisible(false);
        p_back.setVisible(false);
        p_back.setBackground(Color.BLACK);
        
        newGame_Button = new JButton();
            newGame_Button.setPreferredSize(new Dimension(250, 70)); 
            try {
                Image img = ImageIO.read(getClass().getResource("resources/button_new_game.bmp"));
                newGame_Button.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            newGame_Button.setMargin(new Insets(0, 0, 0, 0));
            newGame_Button.setContentAreaFilled(false);
            newGame_Button.setBackground(null);
            newGame_Button.setBorder(null);

        exit_Button = new JButton();
            exit_Button.setPreferredSize(new Dimension(250, 70)); 
            try {
                Image img = ImageIO.read(getClass().getResource("resources/button_exit.bmp"));
                exit_Button.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            exit_Button.setMargin(new Insets(0, 0, 0, 0));
            exit_Button.setContentAreaFilled(false);
            exit_Button.setBackground(null);
            exit_Button.setBorder(null);

        difficulty_Button = new JButton();
             difficulty_Button.setPreferredSize(new Dimension(250, 70)); 
            try {
                Image img = ImageIO.read(getClass().getResource("resources/button_difficulty.bmp"));
                difficulty_Button.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            difficulty_Button.setMargin(new Insets(0, 0, 0, 0));
            difficulty_Button.setContentAreaFilled(false);
            difficulty_Button.setBackground(null);
            difficulty_Button.setBorder(null);

        customize_Button = new JButton();
            customize_Button.setPreferredSize(new Dimension(250, 70)); 
            try {
                Image img = ImageIO.read(getClass().getResource("resources/button_customize.bmp"));
                customize_Button.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            customize_Button.setMargin(new Insets(0, 0, 0, 0));
            customize_Button.setContentAreaFilled(false);
            customize_Button.setBackground(null);
            customize_Button.setBorder(null);

        back_Button = new JButton();
            back_Button.setPreferredSize(new Dimension(250, 70)); 
            try {
                Image img = ImageIO.read(getClass().getResource("resources/button_back.bmp"));
                back_Button.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                System.out.println(ex);
            }
            back_Button.setContentAreaFilled(false);
            back_Button.setBackground(null);
            back_Button.setBorder(null);
            back_Button.setVisible(false);

        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\resources\\mario.ttf"));
        
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        

        title.setFont(new Font("Typeface Mario World Pixel Filled", Font.BOLD, 80));
        score.setFont(new Font("Typeface Mario World Pixel Filled", Font.BOLD, 60));
        panel_title.setLayout(new BorderLayout());
        panel_title.add(title);
        panel_title.add(score);
        add(panel_title, BorderLayout.NORTH);
        score.setForeground(Color.GRAY);
        title.setForeground(Color.WHITE);
        add(win);
        win.setLayout(new BorderLayout());
        win.add(win_P, BorderLayout.CENTER);
        win_P.setHorizontalAlignment(SwingConstants.CENTER);
        add(p_back, BorderLayout.SOUTH);
        p_back.setLayout(new BorderLayout());
        p_back.add(back_Button, BorderLayout.EAST);
        

        
        buttonContainer.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(60, 60, 60, 60);

            // Primo bottone
            gbc.gridx = 0;
            gbc.gridy = 0;
            buttonContainer.add(newGame_Button, gbc);

            // Secondo bottone
            gbc.gridx = 1;
            gbc.gridy = 0;
            buttonContainer.add(exit_Button, gbc);

            // Terzo bottone
            gbc.gridx = 0;
            gbc.gridy = 1;
            buttonContainer.add(difficulty_Button, gbc);

            // Quarto bottone
            gbc.gridx = 1;
            gbc.gridy = 1;
            buttonContainer.add(customize_Button, gbc);

            Record.setSize(500, 500);
            Record.setBackground(Color.GRAY);
            Record.setVisible(false);
            Record.add(R_score);
            add(Record);
            score.setVisible(false);
            panel_title.setBackground(null);
            panel_title.add(title, BorderLayout.CENTER);
            title.setHorizontalAlignment(SwingConstants.CENTER);
            panel_title.add(score, BorderLayout.NORTH);
            score.setHorizontalAlignment(SwingConstants.CENTER);
            add(panel_title, BorderLayout.NORTH);
            add(buttonContainer, BorderLayout.CENTER);

        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    wPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    sPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upPressed = true;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    wPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    sPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    upPressed = false;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    downPressed = false;
                }
            }
        });

        back_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                buttonsVisible = true;
                buttonContainer.setVisible(true);
                title.setVisible(true);
                score.setVisible(false);
                back_Button.setVisible(false);
                changeBall = true;
                ball_X = 350; ball_Y = 220;
                rectangle1_Y = 390;
                rectangle2_Y = 390;
                rettangoliMover.suspendThread();
                repaint();
            }
        });

        newGame_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                buttonsVisible = false;
                p_back.setVisible(true);
                back_Button.setVisible(true);
                buttonContainer.setVisible(false);
                title.setVisible(false);
                score.setVisible(true);
                P1_score = 0;
                P2_score = 0;
                changeBall = false;
                ball_X = 600; ball_Y = 400;
                Record.setVisible(false);
                if(!threadResuming) {
                    rettangoliMover.t.start();
                    threadResuming = true;
                } else {
                    rettangoliMover.resumeThread();
                }
                score.setText(P1_score + "                       " + P2_score);
                repaint();
            }
        });

        back_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_back_1.bmp"));
                    back_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_back.bmp"));
                    back_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        newGame_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_new_game_1.bmp"));
                    newGame_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_new_game.bmp"));
                    newGame_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        exit_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
                try {
                    colorPaletteApp.flushFile("src/color.txt");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        exit_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_exit_1.bmp"));
                    exit_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_exit.bmp"));
                    exit_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        difficulty_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (diff_choice == 0) {
                    diff_choice = 1;
                    sleep_thread = 500;
                    difficult_ = 13;
                } else if (diff_choice == 1) {
                    diff_choice = 2;
                    sleep_thread = 2000;
                    difficult_ = 3;
                } else {
                    diff_choice = 0;
                    sleep_thread = 1000;
                    difficult_ = 7;
                }
                
            }
        });

        difficulty_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (diff_choice == 0) {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_1.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else if (diff_choice == 1) {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_2.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_3.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (diff_choice == 0) {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_1.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else if (diff_choice == 1) {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_2.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                } else {
                    try {
                        Image img = ImageIO.read(getClass().getResource("resources/button_difficulty_3.bmp"));
                        difficulty_Button.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_difficulty.bmp"));
                    difficulty_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        customize_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        colorPaletteApp.setVisible(true);
                    }
                });
            }
        });
        
        colorPaletteApp.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Azione da eseguire all'uscita del JFrame
                colors = colorPaletteApp.readColorsFromFile("src/color.txt");
                getContentPane().setBackground(colors.get(0));
                buttonContainer.setBackground(colors.get(0));
                title.setForeground(colors.get(2));
                currentColor = colors.get(4);
                score.setForeground(colors.get(3));
                p_back.setBackground(colors.get(0));
                win.setBackground(colors.get(1));
                win_P.setForeground(colors.get(2));
            }
        });


        customize_Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_customize_1.bmp"));
                    customize_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    Image img = ImageIO.read(getClass().getResource("resources/button_customize.bmp"));
                    customize_Button.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        setTitle("Pong");
        pack(); // Dimensiona il JFrame in modo appropriato
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setSize(1200, 800);
        setLocation(180, 10);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setUndecorated(true);
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);

    }
}