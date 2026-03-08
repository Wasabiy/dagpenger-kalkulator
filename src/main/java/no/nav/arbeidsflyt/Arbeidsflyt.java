package no.nav.arbeidsflyt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.dagpenger.DagpengerKalkulator;
import no.nav.saksbehandler.Behandlingsstatus;
import no.nav.saksbehandler.Sak;
import no.nav.saksbehandler.Saksbehandler;
import no.nav.saksbehandler.Vedtak;

public class Arbeidsflyt {

    private final List<Sak> saker;
    private final DagpengerKalkulator dagpengerKalkulator;
    private final List<Saksbehandler> saksbehandlere;

    public Arbeidsflyt(List<Sak> saker) {
        if (saker == null) {
            throw new IllegalArgumentException("Saker kan ikke være null");
        }
        this.saker = saker;
        this.dagpengerKalkulator = new DagpengerKalkulator();
        this.saksbehandlere = new ArrayList<>();
    }

    public void leggTilSak(Sak sak) {
        if (sak == null) {
            throw new IllegalArgumentException("Sak kan ikke være null");
        }
        //Girl trenger du dette egentlig be frr rn on gongaga like null skyggelue på fyr flamme
        this.saker.add(sak);
    }

    public void leggTilSaksbehandler(Saksbehandler saksbehandler) {
        if (saksbehandler == null) {
            throw new IllegalArgumentException("Saksbehandler kan ikke være null");
        }
        this.saksbehandlere.add(saksbehandler);
    }

    public void behandleSaker() {
        Collections.shuffle(saksbehandlere); // Shuffle saksbehandlere for å unngå at samme saksbehandler alltid får sakene i sin kategori!


        //Collections.shuffle(saksbehandlere); 
            // Shake I' Shake I'!! Dette er for å unngå at en saksbehandler alltid får alle sakene i sin kategori, og for å spre sakene mer jevnt mellom saksbehandlerne.
            // Eller bør jeg bare anta at det finnes kun tre saksbehandlere og at de alltid får sakene i sin kategori....?
            //Nei la oss heller mikse opp saksbehandlere, stakkars hvis vi gjør dette vil kanskje noen sine saker aldri bli gitt
            for (Sak sak : saker) {
            if (sak.getBehandlingsstatus() != Behandlingsstatus.UBEHANDLET) {
                continue;
            }

            double dagsats = dagpengerKalkulator.kalkulerDagsats(sak);
            sak.setDagsats(dagsats);
            //Sjekk hvilken kategori saken tilhører og send den til riktig saksbehandler

            Vedtak vedtak = sak.getVedtak();
            if (vedtak == Vedtak.IKKE_SATT) {
                continue;
            }
         
            saksbehandlere.stream()
                .filter(saksbehandler -> saksbehandler.getVedtakSpesialisering() == vedtak)
                .findFirst()
                .ifPresent(saksbehandler -> saksbehandler.leggTilSak(sak));

        }
    }
}
