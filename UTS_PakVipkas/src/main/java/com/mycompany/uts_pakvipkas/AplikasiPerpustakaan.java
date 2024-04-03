
package com.mycompany.uts_pakvipkas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Book implements Serializable {
    private String title;
    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author;
    }
}

public class AplikasiPerpustakaan extends JFrame {
    private static final String FILENAME = "library.txt";
    private List<Book> books;
    private JTextField titleField, authorField;
    private JTextArea displayArea;

    public AplikasiPerpustakaan() {
        books = new ArrayList<>();
        loadBooks();

        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel titleLabel = new JLabel("Title:");
        titleField = new JTextField(20);
        JLabel authorLabel = new JLabel("Author:");
        authorField = new JTextField(20);

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(new AddButtonListener());

        JButton searchButton = new JButton("Search Book");
        searchButton.addActionListener(new SearchButtonListener());

        JButton displayButton = new JButton("Display All Books");
        displayButton.addActionListener(new DisplayButtonListener());

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(titleLabel)
                        .addComponent(authorLabel)
                        .addComponent(addButton)
                        .addComponent(searchButton)
                        .addComponent(displayButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(titleField)
                        .addComponent(authorField)
                        .addComponent(scrollPane))
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(titleLabel)
                        .addComponent(titleField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(authorLabel)
                        .addComponent(authorField))
                .addComponent(addButton)
                .addComponent(searchButton)
                .addComponent(displayButton)
                .addComponent(scrollPane)
        );

        add(panel);
    }

    @SuppressWarnings("unchecked")
    private void loadBooks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILENAME))) {
            books = (List<Book>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Failed to load books: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(books);
        } catch (IOException e) {
            System.out.println("Failed to save books: " + e.getMessage());
        }
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (!title.isEmpty() && !author.isEmpty()) {
                books.add(new Book(title, author));
                saveBooks();
                displayArea.append("Book added: " + title + " by " + author + "\n");
                titleField.setText("");
                authorField.setText("");
            } else {
                JOptionPane.showMessageDialog(AplikasiPerpustakaan.this,
                        "Please fill in both title and author fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTitle = titleField.getText().trim();
            if (!searchTitle.isEmpty()) {
                List<Book> searchResult = searchBooksByTitle(searchTitle);
                if (!searchResult.isEmpty()) {
                    displayArea.setText("");
                    searchResult.forEach(book -> displayArea.append(book.toString() + "\n"));
                } else {
                    displayArea.setText("No books found with the given title.");
                }
            } else {
                JOptionPane.showMessageDialog(AplikasiPerpustakaan.this,
                        "Please enter title to search.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DisplayButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            displayAllBooks();
        }
    }

    private List<Book> searchBooksByTitle(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    private void displayAllBooks() {
        displayArea.setText("");
        if (!books.isEmpty()) {
            books.forEach(book -> displayArea.append(book.toString() + "\n"));
        } else {
            displayArea.setText("No books added yet.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AplikasiPerpustakaan gui = new AplikasiPerpustakaan();
            gui.setVisible(true);
        });
    }
}
