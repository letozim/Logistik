package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Auftrag {
    private int auftragId;
    private String auftragsnummer;
    private int kundeId;
    private String kundeName; // Für Anzeige
    private int fahrerId;
    private String fahrerName; // Für Anzeige

    // Lieferinformationen
    private String lieferadresse;
    private LocalDate lieferdatum;

    // Status und Priorität
    private AuftragStatus status;
    private AuftragPrioritaet prioritaet;

    // Finanzielle Informationen
    private BigDecimal gesamtsumme;
    private BigDecimal mwstSatz;

    // Zusätzliche Informationen
    private String bemerkungen;
    private String interneNotizen;

    // Zeitstempel
    private LocalDateTime erstelltAm;
    private LocalDateTime geaendertAm;

    // Auftragspositionen
    private List<AuftragPosition> positionen;

    // Konstruktoren
    public Auftrag() {
        this.status = AuftragStatus.NEU;
        this.prioritaet = AuftragPrioritaet.NORMAL;
        this.gesamtsumme = BigDecimal.ZERO;
        this.mwstSatz = new BigDecimal("19.00");
        this.erstelltAm = LocalDateTime.now();
        this.geaendertAm = LocalDateTime.now();
        this.positionen = new ArrayList<>();
    }

    public Auftrag(int kundeId, String kundeName) {
        this();
        this.kundeId = kundeId;
        this.kundeName = kundeName;
    }

    // Business Logic
    public void addPosition(AuftragPosition position) {
        position.setAuftragId(this.auftragId);
        this.positionen.add(position);
        berechneGesamtsumme();
    }

    public void removePosition(AuftragPosition position) {
        this.positionen.remove(position);
        berechneGesamtsumme();
    }

    public void berechneGesamtsumme() {
        this.gesamtsumme = positionen.stream()
                .map(AuftragPosition::getGesamtpreis)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getGesamtsummeBrutto() {
        BigDecimal netto = getGesamtsumme();
        BigDecimal mwst = netto.multiply(mwstSatz.divide(new BigDecimal("100")));
        return netto.add(mwst);
    }

    public BigDecimal getMwstBetrag() {
        return getGesamtsumme().multiply(mwstSatz.divide(new BigDecimal("100")));
    }

    public int getAnzahlPositionen() {
        return positionen.size();
    }

    public boolean istBearbeitbar() {
        return status == AuftragStatus.NEU || status == AuftragStatus.BESTAETIGT;
    }

    // Getter & Setter
    public int getAuftragId() { return auftragId; }
    public void setAuftragId(int auftragId) { this.auftragId = auftragId; }

    public String getAuftragsnummer() { return auftragsnummer; }
    public void setAuftragsnummer(String auftragsnummer) { this.auftragsnummer = auftragsnummer; }

    public int getKundeId() { return kundeId; }
    public void setKundeId(int kundeId) { this.kundeId = kundeId; }

    public String getKundeName() { return kundeName; }
    public void setKundeName(String kundeName) { this.kundeName = kundeName; }

    public int getFahrerId() { return fahrerId; }
    public void setFahrerId(int fahrerId) { this.fahrerId = fahrerId; }

    public String getFahrerName() { return fahrerName; }
    public void setFahrerName(String fahrerName) { this.fahrerName = fahrerName; }

    public String getLieferadresse() { return lieferadresse; }
    public void setLieferadresse(String lieferadresse) { this.lieferadresse = lieferadresse; }

    public LocalDate getLieferdatum() { return lieferdatum; }
    public void setLieferdatum(LocalDate lieferdatum) { this.lieferdatum = lieferdatum; }

    public AuftragStatus getStatus() { return status; }
    public void setStatus(AuftragStatus status) { this.status = status; }

    public AuftragPrioritaet getPrioritaet() { return prioritaet; }
    public void setPrioritaet(AuftragPrioritaet prioritaet) { this.prioritaet = prioritaet; }

    public BigDecimal getGesamtsumme() { return gesamtsumme; }
    public void setGesamtsumme(BigDecimal gesamtsumme) { this.gesamtsumme = gesamtsumme; }

    public BigDecimal getMwstSatz() { return mwstSatz; }
    public void setMwstSatz(BigDecimal mwstSatz) { this.mwstSatz = mwstSatz; }

    public String getBemerkungen() { return bemerkungen; }
    public void setBemerkungen(String bemerkungen) { this.bemerkungen = bemerkungen; }

    public String getInterneNotizen() { return interneNotizen; }
    public void setInterneNotizen(String interneNotizen) { this.interneNotizen = interneNotizen; }

    public LocalDateTime getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(LocalDateTime erstelltAm) { this.erstelltAm = erstelltAm; }

    public LocalDateTime getGeaendertAm() { return geaendertAm; }
    public void setGeaendertAm(LocalDateTime geaendertAm) { this.geaendertAm = geaendertAm; }

    public List<AuftragPosition> getPositionen() { return new ArrayList<>(positionen); }
    public void setPositionen(List<AuftragPosition> positionen) {
        this.positionen = new ArrayList<>(positionen);
        berechneGesamtsumme();
    }
}