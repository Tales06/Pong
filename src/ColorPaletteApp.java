import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ColorPaletteApp extends JFrame {
    protected JPanel colorPanel;
    protected JButton chooseColorButton;

    public ColorPaletteApp() {
        super("Color Palette App");
        setSize(400, 230);

        colorPanel = new JPanel();
        colorPanel.setBackground(Color.BLACK);
        chooseColorButton = new JButton();
        chooseColorButton.setSize(400, 200);
        try {
            Image img = ImageIO.read(getClass().getResource("resources/button_color.bmp"));
            chooseColorButton.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        chooseColorButton.setContentAreaFilled(false);
        chooseColorButton.setBackground(null);
        chooseColorButton.setBorder(null);
        // chooseColorButton.setVisible(false);

        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color selectedColor = JColorChooser.showDialog(ColorPaletteApp.this, "Choose a Color", Color.WHITE);
                if (selectedColor != null) {
                    updateColorPalette(selectedColor);
                }
            }
        });

        setLayout(new BorderLayout());
        add(chooseColorButton, BorderLayout.CENTER);
        add(colorPanel, BorderLayout.CENTER);
        setLocation(800, 500);
    }

    protected void updateColorPalette(Color baseColor) {
        colorPanel.removeAll();

        colorPanel.setBackground(baseColor);

        // Calcola colori complementari o simili
        Color[] relatedColors = calculateRelatedColors(baseColor);

        for (Color color : relatedColors) {
            JPanel colorBox = new JPanel();
            colorBox.setBackground(color);
            colorPanel.add(colorBox);
        }

        colorPanel.revalidate();
        colorPanel.repaint();
    }

    protected Color[] calculateRelatedColors(Color baseColor) {
        // Implementa la logica per calcolare colori correlati.
        // In questo esempio, vengono generati colori leggermente più chiari e più scuri.
        Color[] relatedColors = new Color[5];
        float[] hsb = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
        String colorString;
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("src/color.txt");

            for (int i = 1; i <= 5; i++) {
                float brightness = hsb[2] - 0.1f * i;
                brightness = Math.max(0, Math.min(1, brightness)); // Ensure brightness is between 0 and 1
                relatedColors[i - 1] = Color.getHSBColor(hsb[0], hsb[1], brightness);
                colorString = colorToString(relatedColors[i - 1]);

                if (i == 1) {
                    colorString = colorToString(baseColor);
                    myWriter.write(colorString+"\n");
                    colorString = colorToString(relatedColors[i - 1]);
                }

                myWriter.write(colorString);

                if (i < 5) {
                    myWriter.write("\n"); // Add a newline character after each color value, except the last one
                }
            }

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            if (myWriter != null) {
                try {
                     myWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return relatedColors;

    }

    public ArrayList<Color> readColorsFromFile(String filePath) {
        ArrayList<Color> colors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            if((line = reader.readLine()) != null) {
                while ((line = reader.readLine()) != null) {
                    String[] rgbValues = line.split(",");
                    if (rgbValues.length == 3) {
                        int red = Integer.parseInt(rgbValues[0]);
                        int green = Integer.parseInt(rgbValues[1]);
                        int blue = Integer.parseInt(rgbValues[2]);
                        Color color = new Color(red, green, blue);
                        colors.add(color);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Non esiste alcun colore", "Color Palette", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return colors;
    }

    protected void flushFile(String filePath) throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String colorToString(Color color) {
        // Formato RGB: "red,green,blue"
        return String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue());
    }

    
    public Color[] getRelatedColors(Color baseColor) {
        return calculateRelatedColors(baseColor);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ColorPaletteApp().setVisible(true);
            }
        });
    }
}