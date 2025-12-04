package com.example.myapplication.utils;

public class KategoriDetector {

    public static String deteksiKategori(String deskripsi) {
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            return "Lainnya";
        }
        String lower = deskripsi.toLowerCase();

        // === KATEGORI: TRANSPORTASI (dicek DULU karena lebih spesifik) ===
        if (lower.contains("gojek") ||
                lower.contains("grab") ||
                lower.contains("grabcar") ||
                lower.contains("grabbike") || // ini TRANSPORT, bukan makanan!
                lower.contains("ojek") ||
                lower.contains("taxi") ||
                lower.contains("taksi") ||
                lower.contains("kereta") ||
                lower.contains("krl") ||
                lower.contains("commuter") ||
                lower.contains("angkot") ||
                lower.contains("bemo") ||
                lower.contains("bis") ||
                lower.contains("bus") ||
                lower.contains("damri") ||
                lower.contains("travel") ||
                lower.contains("pesawat") ||
                lower.contains("flight") ||
                lower.contains("kapal") ||
                lower.contains("feri") ||
                lower.contains("spbu") ||
                lower.contains("pom bensin") ||
                lower.contains("bensin") ||
                lower.contains("pertamina") ||
                lower.contains("shell") ||
                lower.contains("tol") ||
                lower.contains("toll") ||
                lower.contains("parkir") ||
                lower.contains("e-toll") ||
                lower.contains("flazz") ||
                lower.contains("brizzi") ||
                lower.contains("jaklingko") ||
                lower.contains("mrt") ||
                lower.contains("lrt") ||
                lower.contains("transjakarta") ||
                lower.contains("busway") ||
                lower.contains("ojol")) {
            return "Transportasi";
        }

        // === KATEGORI: MAKANAN ===
        if (lower.contains("makan") ||
                lower.contains("nasi") ||
                lower.contains("ayam") ||
                lower.contains("bakso") ||
                lower.contains("soto") ||
                lower.contains("mie") ||
                lower.contains("noodle") ||
                lower.contains("ramen") ||
                lower.contains("sate") ||
                lower.contains("goreng") ||
                lower.contains("rendang") ||
                lower.contains("sambal") ||
                lower.contains("es") ||
                lower.contains("teh") ||
                lower.contains("kopi") ||
                lower.contains("cafe") ||
                lower.contains("coffee") ||
                lower.contains("warung") || // hanya di sini
                lower.contains("resto") ||
                lower.contains("restoran") ||
                lower.contains("rumah makan") ||
                lower.contains("warkop") ||
                lower.contains("kantin") ||
                lower.contains("mcdonald") ||
                lower.contains("mcd") ||
                lower.contains("kfc") ||
                lower.contains("burger") ||
                lower.contains("pizza") ||
                lower.contains("domino") ||
                lower.contains("jco") ||
                lower.contains("dunkin") ||
                lower.contains("starbucks") ||
                lower.contains("gofood") || // gofood/grabfood = makanan
                lower.contains("grabfood") ||
                lower.contains("shopeefood") ||
                lower.contains("food") ||
                lower.contains("catering") ||
                lower.contains("sushi") ||
                lower.contains("rawon") ||
                lower.contains("gado") ||
                lower.contains("pecel") ||
                lower.contains("ketoprak") ||
                lower.contains("lontong") ||
                lower.contains("bakmi") ||
                lower.contains("martabak") ||
                lower.contains("roti") ||
                lower.contains("siomay") ||
                lower.contains("batagor") ||
                lower.contains("cilok")) {
            return "Makanan";
        }

        // === KATEGORI: BELANJA (termasuk semua dari "Pulsa & Data") ===
        if (lower.contains("belanja") ||
                lower.contains("toko") ||
                lower.contains("shop") ||
                lower.contains("mart") ||
                lower.contains("alfamart") ||
                lower.contains("indomaret") ||
                lower.contains("yomart") ||
                lower.contains("7-eleven") ||
                lower.contains("minimarket") ||
                lower.contains("sembako") ||
                lower.contains("baju") ||
                lower.contains("celana") ||
                lower.contains("sepatu") ||
                lower.contains("tas") ||
                lower.contains("makeup") ||
                lower.contains("kosmetik") ||
                lower.contains("shopee") ||
                lower.contains("tokopedia") ||
                lower.contains("bukalapak") ||
                lower.contains("lazada") ||
                lower.contains("blibli") ||
                lower.contains("amazon") ||
                lower.contains("tiktok shop") ||
                lower.contains("online shop") ||
                lower.contains("store") ||
                lower.contains("barang") ||
                lower.contains("perlengkapan") ||
                lower.contains("pasar") ||
                // --- TAMBAHAN BELANJA ---
                lower.contains("ace hardware") ||
                lower.contains("informa") ||
                lower.contains("hypermart") ||
                lower.contains("carrefour") ||
                lower.contains("transmart") ||
                lower.contains("farmasi") ||
                lower.contains("apotek") ||
                lower.contains("k24") ||
                lower.contains("guardian") ||
                lower.contains("watsons") ||
                lower.contains("wardah") ||
                lower.contains("scarlett") ||
                lower.contains("natura") ||
                lower.contains("fashion") ||
                lower.contains("gadget") ||
                lower.contains("elektronik") ||
                (lower.contains("hp") && (lower.contains("beli") || lower.contains("belanja") || lower.contains("shopee") || lower.contains("tokopedia"))) ||
                lower.contains("laptop") ||
                lower.contains("printer") ||
                lower.contains("mainan") ||
                lower.contains("perlengkapan bayi") ||
                lower.contains("deterjen") ||
                lower.contains("sabun") ||
                lower.contains("shampoo") ||
                lower.contains("pampers") ||
                (lower.contains("susu") && !lower.contains("kopi") && !lower.contains("cafe")) ||
                lower.contains("popok") ||
                lower.contains("obat") ||
                lower.contains("vitamin") ||
                (lower.contains("bayar") && (lower.contains("toko") || lower.contains("mart") || lower.contains("shop"))) ||
                lower.contains("checkout") ||
                lower.contains("keranjang") ||
                lower.contains("ongkir") ||
                (lower.contains("diskon") && (lower.contains("shopee") || lower.contains("tokopedia"))) ||
                // === SEMUA KATA KUNCI DARI "PULSA & DATA" DIPINDAH KE SINI ===
                lower.contains("pulsa") ||
                lower.contains("kuota") ||
                lower.contains("data") ||
                lower.contains("internet") ||
                lower.contains("paket data") ||
                lower.contains("telkomsel") ||
                lower.contains("xl") ||
                lower.contains("indosat") ||
                lower.contains("tri") ||
                lower.contains("smartfren") ||
                lower.contains("axis") ||
                lower.contains("by.u") ||
                lower.contains("om telolet om") ||
                lower.contains("mytelkomsel") ||
                lower.contains("myxl") ||
                lower.contains("myindihome") ||
                (lower.contains("gopay") && (lower.contains("pulsa") || lower.contains("kuota"))) ||
                (lower.contains("dana") && (lower.contains("pulsa") || lower.contains("kuota"))) ||
                (lower.contains("shopeepay") && (lower.contains("pulsa") || lower.contains("kuota"))) ||
                (lower.contains("ovo") && (lower.contains("pulsa") || lower.contains("kuota"))) ||
                (lower.contains("top up") && (lower.contains("pulsa") || lower.contains("kuota") || lower.contains("telkomsel"))) ||
                lower.contains("beli pulsa") ||
                lower.contains("isi ulang") ||
                lower.contains("paket internet") ||
                lower.contains("paket nelpon") ||
                (lower.contains("sms") && (lower.contains("pulsa") || lower.contains("promo"))) ||
                lower.contains("quota") ||
                lower.contains("mbps") ||
                (lower.contains("gb") && (lower.contains("internet") || lower.contains("data"))) ||
                (lower.contains("unlimited") && lower.contains("internet"))) {
            return "Belanja";
        }

        // === KATEGORI: HIBURAN (tetap seperti aslinya) ===
        if (lower.contains("bioskop") ||
                lower.contains("cgv") ||
                lower.contains("xxi") ||
                lower.contains("film") ||
                lower.contains("game") ||
                lower.contains("playstation") ||
                lower.contains("ps4") ||
                lower.contains("ps5") ||
                lower.contains("steam") ||
                lower.contains("mobile legend") ||
                lower.contains("mlbb") ||
                lower.contains("free fire") ||
                lower.contains("valorant") ||
                lower.contains("netflix") ||
                lower.contains("spotify") ||
                lower.contains("youtube premium") ||
                lower.contains("disney+") ||
                lower.contains("prime video") ||
                lower.contains("vidio") ||
                lower.contains("konser") ||
                lower.contains("event") ||
                lower.contains("tiket event") ||
                // --- TAMBAHAN HIBURAN ---
                lower.contains("tiket.com") ||
                lower.contains("traveloka") ||
                lower.contains("eventbrite") ||
                (lower.contains("tiket") && !lower.contains("pesawat") && !lower.contains("kereta") && !lower.contains("bus")) ||
                lower.contains("arena") ||
                lower.contains("karaoke") ||
                lower.contains("bowling") ||
                lower.contains("escape room") ||
                lower.contains("taman bermain") ||
                lower.contains("dunia fantasi") ||
                lower.contains("dago") ||
                (lower.contains("mall") && (lower.contains("bioskop") || lower.contains("game") || lower.contains("arena"))) ||
                (lower.contains("gopay") && (lower.contains("cgv") || lower.contains("xxi") || lower.contains("game"))) ||
                (lower.contains("dana") && (lower.contains("netflix") || lower.contains("spotify"))) ||
                lower.contains("playstore") ||
                lower.contains("app store") ||
                lower.contains("google play") ||
                lower.contains("in-app purchase") ||
                lower.contains("diamond") ||
                lower.contains("skin") ||
                lower.contains("battle pass") ||
                (lower.contains("top up") && (lower.contains("game") || lower.contains("mlbb") || lower.contains("free fire"))) ||
                lower.contains("e-sport") ||
                lower.contains("tonton") ||
                lower.contains("streaming")) {
            return "Hiburan";
        }

        // === DEFAULT ===
        return "Lainnya";
    }
}