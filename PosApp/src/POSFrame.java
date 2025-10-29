import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class POSFrame extends JFrame {
    private JTable tableProduk, tableKeranjang;
    private DefaultTableModel modelProduk, modelKeranjang;
    private JTextField txtQty;
    private JLabel lblTotal, lblPoints;
    private JTextArea areaStruk;

    public POSFrame() {
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // tampil di tengah layar

        // ===== PANEL KIRI: PRODUK =====
        JPanel panelKiri = new JPanel(new BorderLayout());
        panelKiri.setBorder(BorderFactory.createTitledBorder("Produk"));

        // Tabel produk
        String[] kolomProduk = {"ID", "Nama Produk", "Harga (Rp)"};
        Object[][] dataProduk = {
                {"P001", "Gorengan", 1500},
                {"P002", "Kopi", 2000},
                {"P003", "Roti", 4000},
                {"P004", "Keripik", 5500},
                {"P005", "Minuman", 9000}
        };
        modelProduk = new DefaultTableModel(dataProduk, kolomProduk);
        tableProduk = new JTable(modelProduk);
        panelKiri.add(new JScrollPane(tableProduk), BorderLayout.CENTER);

        // Input qty dan tombol tambah
        JPanel panelBawahKiri = new JPanel();
        panelBawahKiri.add(new JLabel("Qty:"));
        txtQty = new JTextField("1", 5);
        panelBawahKiri.add(txtQty);
        JButton btnAddToCart = new JButton("Add to Cart");
        panelBawahKiri.add(btnAddToCart);
        panelKiri.add(panelBawahKiri, BorderLayout.SOUTH);

        // ===== PANEL KANAN: KERANJANG =====
        JPanel panelKanan = new JPanel(null);
        panelKanan.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        // Tabel keranjang
        String[] kolomKeranjang = {"ID", "Nama Produk", "Qty", "Harga", "Subtotal"};
        modelKeranjang = new DefaultTableModel(kolomKeranjang, 0);
        tableKeranjang = new JTable(modelKeranjang);
        JScrollPane scrollKeranjang = new JScrollPane(tableKeranjang);
        scrollKeranjang.setBounds(20, 30, 500, 150);
        panelKanan.add(scrollKeranjang);

        // Label total dan points
        lblTotal = new JLabel("Total: Rp0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setBounds(20, 200, 200, 30);
        panelKanan.add(lblTotal);

        lblPoints = new JLabel("Points: 0");
        lblPoints.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPoints.setBounds(20, 230, 200, 30);
        panelKanan.add(lblPoints);

        // Tombol checkout dan cetak
        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.setBounds(20, 270, 120, 30);
        panelKanan.add(btnCheckout);

        JButton btnCetak = new JButton("Cetak");
        btnCetak.setBounds(150, 270, 120, 30);
        panelKanan.add(btnCetak);

        // Area struk
        JLabel lblStruk = new JLabel("Struk:");
        lblStruk.setBounds(20, 320, 200, 20);
        panelKanan.add(lblStruk);

        areaStruk = new JTextArea();
        areaStruk.setEditable(false);
        JScrollPane scrollStruk = new JScrollPane(areaStruk);
        scrollStruk.setBounds(20, 340, 500, 180);
        panelKanan.add(scrollStruk);

        // ===== SPLIT PANE =====
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelKiri, panelKanan);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        // tampilkan frame
        setVisible(true);
    }

    public static void main(String[] args) {
        new POSFrame();
    }
}
