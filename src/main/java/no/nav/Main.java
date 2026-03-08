package no.nav;

import java.util.ArrayList;
import java.util.List;

import no.nav.arbeidsflyt.Arbeidsflyt;
import no.nav.dagpenger.DagpengerKalkulator;
import no.nav.saksbehandler.Sak;
import no.nav.saksbehandler.Saksbehandler;
import no.nav.saksbehandler.Vedtak;
import no.nav.årslønn.Årslønn;

public class Main {
    public static void main(String[] args) {  DagpengerKalkulator dagpengerKalkulator = new DagpengerKalkulator();
        Sak sak = new Sak(1L, "Ola Nordmann", 123456789);
        List<Årslønn> årslønner = new ArrayList<>();
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2023, 500000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2022, 450000));
        dagpengerKalkulator.leggTilÅrslønn(årslønner, new Årslønn(2021, 400000));
        sak.setÅrslønner(årslønner);
        System.out.println("---🤖 Kalkulerer dagsats... 🤖---");
        System.out.println("Personen har rett på følgende dagsats: " + dagpengerKalkulator.kalkulerDagsats(sak));
        System.out.println("---🤖 Dagsats ferdig kalkulert 🤖---");
        List<Sak> saker = new ArrayList<>();
        saker.add(opprettSak(
                13,
                "Innvilget Person",
                11111111111L,
                List.of(
                        new Årslønn(2025, 500000),
                        new Årslønn(2024, 450000),
                        new Årslønn(2023, 400000)
                )
        ));
        saker.add(opprettSak(
                2L,
                "Makssats Person",
                22222222222L,
                List.of(
                        new Årslønn(2025, 1200000),
                        new Årslønn(2024, 100000),
                        new Årslønn(2023, 90000)
                )
        ));
        saker.add(opprettSak(
                3L,
                "Avslag Person",
                33333333333L,
                List.of(
                        new Årslønn(2025, 80000),
                        new Årslønn(2024, 100000),
                        new Årslønn(2023, 70000)
                )
        ));

        Arbeidsflyt arbeidsflyt = new Arbeidsflyt(saker);
        Saksbehandler innvilgetBehandler = new Saksbehandler("sb-innvilget", Vedtak.INNVILGET);
        Saksbehandler makssatsBehandler = new Saksbehandler("sb-maks", Vedtak.INNVILGET_MED_MAKSSATS);
        Saksbehandler avslagBehandler = new Saksbehandler("sb-avslag", Vedtak.AVSLAG);

        arbeidsflyt.leggTilSaksbehandler(innvilgetBehandler);
        arbeidsflyt.leggTilSaksbehandler(makssatsBehandler);
        arbeidsflyt.leggTilSaksbehandler(avslagBehandler);
        arbeidsflyt.behandleSaker();

        innvilgetBehandler.hentUbehandledeSakerInnenforSpesialisering(saker).forEach(innvilgetBehandler::godkjennSak);
        makssatsBehandler.hentUbehandledeSakerInnenforSpesialisering(saker).forEach(makssatsBehandler::godkjennSak);
        avslagBehandler.hentUbehandledeSakerInnenforSpesialisering(saker).forEach(avslagBehandler::avslåSak);

        for (Sak sak1 : saker) {
            System.out.printf(
                    "Sak %d (%s): vedtak=%s, behandlingsstatus=%s, dagsats=%.0f%n",
                    sak1.getId(),
                    sak1.getNavn(),
                    sak1.getVedtak(),
                    sak1.getBehandlingsstatus(),
                    sak1.getDagsats()
            );
        }
    }

    private static Sak opprettSak(long id, String navn, long fødselsnummer, List<Årslønn> årslønner) {
        Sak sak = new Sak(id, navn, fødselsnummer);
        sak.setÅrslønner(new ArrayList<>(årslønner));
        return sak;
    }
}
