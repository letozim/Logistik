package org.example.service;

import org.example.model.Ware;
import org.example.repository.WarenRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarenService {

    private WarenRepository warenRepository = new WarenRepository();

    public List<Ware> getAlleWaren() {
        return warenRepository.findAll();
    }


    public void wareHinzufuegen(Ware ware) {
        // Hier können Validierungen hin, z.B. prüfen, ob Artikelnummer schon existiert
        warenRepository.save(ware);
    }

    public Ware wareAktualisieren(Ware ware) {
        // Beispielvalidierung: Menge darf nicht negativ sein
        if (ware.getMenge() < 0) {
            throw new IllegalArgumentException("Menge darf nicht negativ sein");
        }

        // Prüfen, ob Ware mit Artikelnummer existiert
        Ware existierendeWare = warenRepository.findByArtikelnummer(ware.getArtikelnummer());
        if (existierendeWare == null) {
            throw new IllegalArgumentException("Ware mit Artikelnummer " + ware.getArtikelnummer() + " existiert nicht.");
        }

        warenRepository.update(ware);

        // Optional: frisch aus DB zurückgeben, falls z.B. Trigger in DB Felder verändern
        return warenRepository.findByArtikelnummer(ware.getArtikelnummer());
    }


    public void wareLoeschen(String artikelnummer) {
        warenRepository.delete(artikelnummer);
    }
}