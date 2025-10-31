import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class POSApp extends JFrame {
    private JTable tableProduk, tableKeranjang;
    private DefaultTableModel modelProduk, modelKeranjang;
    private JTextField txtQty;
    private JLabel lblTotal, lblPoints, lblSelectedProduct;
    private JTextArea areaStruk;
    private double total = 0;
    private int points = 0;

    private static final Locale LOCALE_ID = new Locale("id", "ID");
    private static final NumberFormat CURR = NumberFormat.getCurrencyInstance(LOCALE_ID);

    public POSApp() {
        setTitle("POS Sederhana - GUI");
        setSize(920, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // === PANEL PRODUK ===
        JPanel panelKiri = new JPanel(new BorderLayout());
        panelKiri.setBorder(BorderFactory.createTitledBorder("Daftar Produk"));

        String[] kolomProduk = {"ID", "Nama Produk", "Harga (Rp)"};
        Object[][] dataProduk = {
                {"P001", "Air Mineral 600ml", 3000},
                {"P002", "Kopi Sachet", 5000},
                {"P003", "Roti Isi", 8000},
                {"P004", "Snack Keripik", 6000},
                {"P005", "Minuman Botol", 12000}
        };
        modelProduk = new DefaultTableModel(dataProduk, kolomProduk);
        tableProduk = new JTable(modelProduk);
        panelKiri.add(new JScrollPane(tableProduk), BorderLayout.CENTER);

        // === PANEL BAWAH UNTUK PILIHAN DAN ADD TO CART ===
        JPanel panelBawah = new JPanel(new GridLayout(2, 1));
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lblSelectedProduct = new JLabel("Belum ada produk dipilih");
        lblSelectedProduct.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSelectedProduct.setForeground(new Color(0, 102, 204));
        panelInfo.add(lblSelectedProduct);
        panelBawah.add(panelInfo);

        JPanel panelTambah = new JPanel();
        panelTambah.add(new JLabel("Qty:"));
        txtQty = new JTextField("1", 5);
        panelTambah.add(txtQty);
        JButton btnAdd = new JButton("Add to Cart");
        panelTambah.add(btnAdd);
        panelBawah.add(panelTambah);

        panelKiri.add(panelBawah, BorderLayout.SOUTH);

        // === PANEL KANAN (KERANJANG & STRUK) ===
        JPanel panelKanan = new JPanel(null);
        panelKanan.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));

        String[] kolomKeranjang = {"ID", "Nama Produk", "Qty", "Harga", "Subtotal"};
        modelKeranjang = new DefaultTableModel(kolomKeranjang, 0);
        tableKeranjang = new JTable(modelKeranjang);
        JScrollPane scrollKeranjang = new JScrollPane(tableKeranjang);
        scrollKeranjang.setBounds(10, 20, 560, 150);
        panelKanan.add(scrollKeranjang);

        lblTotal = new JLabel("Total: Rp0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setBounds(10, 180, 300, 30);
        panelKanan.add(lblTotal);

        lblPoints = new JLabel("Points: 0");
        lblPoints.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPoints.setBounds(10, 210, 300, 30);
        panelKanan.add(lblPoints);

        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.setBounds(10, 250, 120, 30);
        panelKanan.add(btnCheckout);

        JButton btnCetak = new JButton("Cetak Struk");
        btnCetak.setBounds(140, 250, 120, 30);
        panelKanan.add(btnCetak);

        JLabel lblStruk = new JLabel("Struk Pembelian:");
        lblStruk.setBounds(10, 290, 200, 20);
        panelKanan.add(lblStruk);

        areaStruk = new JTextArea();
        areaStruk.setEditable(false);
        areaStruk.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollStruk = new JScrollPane(areaStruk);
        scrollStruk.setBounds(10, 310, 560, 230);
        panelKanan.add(scrollStruk);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelKiri, panelKanan);
        splitPane.setDividerLocation(320);
        add(splitPane, BorderLayout.CENTER);

        // === EVENT LISTENER ===
        tableProduk.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableProduk.getSelectedRow();
            if (selectedRow != -1) {
                String nama = modelProduk.getValueAt(selectedRow, 1).toString();
                double harga = Double.parseDouble(modelProduk.getValueAt(selectedRow, 2).toString());
                lblSelectedProduct.setText("Dipilih: " + nama + " - " + CURR.format(harga));
            }
        });

        btnAdd.addActionListener(e -> addToCart());
        btnCheckout.addActionListener(e -> checkout());
        btnCetak.addActionListener(e -> cetakStruk());

        setVisible(true);
    }

    private void addToCart() {
        int selected = tableProduk.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Pilih produk terlebih dahulu!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah harus angka positif!");
            return;
        }

        String id = modelProduk.getValueAt(selected, 0).toString();
        String nama = modelProduk.getValueAt(selected, 1).toString();
        double harga = Double.parseDouble(modelProduk.getValueAt(selected, 2).toString());
        double subtotal = harga * qty;
        total += subtotal;

        modelKeranjang.addRow(new Object[]{id, nama, qty, harga, subtotal});
        lblTotal.setText("Total: " + CURR.format(total));

        // 🎯 Poin per 1.000 rupiah
        points = (int) (total / 1000);
        lblPoints.setText("Points: " + points);

        txtQty.setText("1");
    }

    private void checkout() {
        if (modelKeranjang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
            return;
        }

        String toko = "HASBY MART";
        String alamat = "Jl. Ciwaruga No. 45, Bandung";
        String waktu = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss", LOCALE_ID));

        String garis = "===================================================";
        String garisKecil = "---------------------------------------------------";

        StringBuilder sb = new StringBuilder();
        sb.append(centerText(toko, 47)).append("\n");
        sb.append(centerText(alamat, 47)).append("\n");
        sb.append(centerText("STRUK PEMBELIAN", 47)).append("\n");
        sb.append(centerText(waktu, 47)).append("\n");
        sb.append(garis).append("\n");
        sb.append(String.format("%-25s %5s %15s\n", "Barang", "Qty", "Subtotal"));
        sb.append(garisKecil).append("\n");

        for (int i = 0; i < modelKeranjang.getRowCount(); i++) {
            String nama = modelKeranjang.getValueAt(i, 1).toString();
            int qty = Integer.parseInt(modelKeranjang.getValueAt(i, 2).toString());
            double subtotal = Double.parseDouble(modelKeranjang.getValueAt(i, 4).toString());
            if (nama.length() > 25) nama = nama.substring(0, 22) + "...";
            sb.append(String.format("%-25s %5d %15s\n", nama, qty, CURR.format(subtotal)));
        }

        sb.append(garisKecil).append("\n");
        sb.append(String.format("%-25s %5s %15s\n", "TOTAL", "", CURR.format(total)));
        sb.append(String.format("%-25s %5s %15d\n", "POINTS", "", points));
        sb.append(garis).append("\n");
        sb.append(centerText("Terima kasih atas kunjungan Anda!", 47)).append("\n");
        sb.append(centerText("Barang yang sudah dibeli tidak dapat dikembalikan.", 47)).append("\n");

        areaStruk.setText(sb.toString());

        // reset
        modelKeranjang.setRowCount(0);
        total = 0;
        points = 0;
        lblTotal.setText("Total: Rp0,00");
        lblPoints.setText("Points: 0");
    }

    private String centerText(String text, int width) {
        int padding = Math.max(0, (width - text.length()) / 2);
        return " ".repeat(padding) + text;
    }

    private void cetakStruk() {
        if (areaStruk.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada struk! Tekan Checkout dulu.");
            return;
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Struk Pembelian");

        job.setPrintable((g, pf, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            int y = 20;
            for (String line : areaStruk.getText().split("\n")) {
                g2.drawString(line, 0, y);
                y += 15;
            }
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Struk berhasil dicetak!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(POSApp::new);
    }
}
