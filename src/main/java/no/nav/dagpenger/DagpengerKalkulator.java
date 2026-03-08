package no.nav.dagpenger;

import java.util.Comparator;
import java.util.List;

import no.nav.grunnbeløp.GrunnbeløpVerktøy;
import no.nav.saksbehandler.Sak;
import no.nav.saksbehandler.Vedtak;
import no.nav.årslønn.Årslønn;

/**
 * Kalkulator for å beregne hvor mye dagpenger en person har rett på i Norge basert på dagens grunnbeløp (1G).
 * For at en person skal ha rett på dagpenger, må en av de to følgene kravene være møtt:
 *      De siste 3 årene må gjennomsnitslønnen være høyere enn 3G.
 *      Tjent mer det siste året enn 1.5G.
 * Hvis en person har rett på dagpenger, må følgende ting vurderes for å kalkulere dagsatsen:
 *      Hva er størst av gjennomsnittlig årslønn de 3 siste årene og siste årslønn.
 *      Hvis siste årslønn er størst, er årslønnen høyere enn 6G.
 * Antall årlige arbeidsdager i Norge er satt til å være 260, så ved beregning av dagsats må 260 dager
 * brukes og ikke 365.
 *
 * @author Emil Elton Nilsen
 * @version 1.0
 */
public class DagpengerKalkulator {

/*
DEL 2 TANKER:

Hvis saksbehandlere skal kun håndtere saker innen deres spesialisering, burde det være en klassifisering resultat, eller at de sendes til saksbehandlere automatisk etter at harRettPåDagpenger funksjonen aktiveres? Trenger muligens en type workflow som skal la deg gjøre dette da. Kanskje en ide kan være å lage enum + en "Saker + Kalkulator klasse" som automatisk kjører gjennom alle sakene og sender det til riktig saksbehandler basert på resultatet av harRettPåDagpenger funksjonen. Saksbehandler bør også kunne få hele saken og verifisere selv hvis de ønsker for å dobbeltsjekke + skrive ned avslaget/innvilgelsen.
*/

    private static final int arbeidsdagerIÅret = 260;

    private final GrunnbeløpVerktøy grunnbeløpVerktøy; 

    //private final List<Årslønn> årslønner; //Gjøre i det minste årslønnerregisteret til en private variabel. Dumt om andre klasser kan endre kalkulatorens årslønner uten å gå gjennom kalkulatoren sine metoder.

    //Jeg er usikker på om jeg burde beholde årslønner som et felt i det som burde være en statisk klasse (kakulator bør kunne brukes flere ganger av samme saksbehandler, tross alt metodene er der bare for å verifisere årslønn og dagsats, alle metoder returnerrer )

    public DagpengerKalkulator() {
        this.grunnbeløpVerktøy = new GrunnbeløpVerktøy();
        //this.årslønner = new ArrayList<Årslønn>();
    }

    /**
     * Hvis en person har rett på dagpenger, vil den kalkulere dagsatsen en person har rett på.
     * Hvis ikke en person har rett på dagpenger, vil metoden returnere 0kr som dagsats, som en antagelse på at det
     * er det samme som å ikke ha rett på dagpenger.
     * @return dagsatsen en person har rett på.
     */
    public double kalkulerDagsats(Sak sak) {
        List<Årslønn> årslønner = sak.getÅrslønner();
        double dagsats = 0;
        //Tror det er greit å beholde variabelen for å debugge / logge underveis hva som skjer med variabelen med kalkulering 
       
        //Kan bruke switch case siden det er bruk av else if setninger
        //Dette betyr at koden ikke trenger å sjekke ytterligere betingelser når en betingelse er møtt.
        if(!harRettigheterTilDagpenger(sak)) {
            sak.setVedtak(Vedtak.AVSLAG);
            return dagsats;
        }else{
            BeregningsMetode beregningsMetode = velgBeregningsMetode(årslønner);
            sorterÅrslønnerBasertPåNyesteÅrslønn(årslønner);
            switch (beregningsMetode) {
                case SISTE_ÅRSLØNN:
                     sak.setVedtak(Vedtak.INNVILGET);
                     dagsats = Math.ceil(hentÅrslønnVedIndeks(årslønner, 0).hentÅrslønn() / arbeidsdagerIÅret);
                     return dagsats;
                case GJENNOMSNITTET_AV_TRE_ÅR:
                    sak.setVedtak(Vedtak.INNVILGET);
                    dagsats = Math.ceil((summerNyligeÅrslønner(3, årslønner) / 3) / arbeidsdagerIÅret);
                    return dagsats;
                case MAKS_ÅRLIG_DAGPENGERGRUNNLAG:
                    sak.setVedtak(Vedtak.INNVILGET_MED_MAKSSATS);
                    dagsats = Math.ceil(grunnbeløpVerktøy.hentMaksÅrligDagpengegrunnlag() / arbeidsdagerIÅret);
                    return dagsats;
                default:
                    throw new AssertionError(); //
            }
        }
    }

    /**
     * Sjekker om en person har rettighet til dagpenger eller ikke.
     * @return om personen har rett på dagpenger.
     */
    public boolean harRettigheterTilDagpenger(Sak sak) {
        List<Årslønn> årslønner = sak.getÅrslønner();
        // Det er unødvendig å definere en boolean variabel heller returner direkte verdien
        // if else if statement sier til oss at vi ikke må sjekke for begge og kan gå videre hvis en inntreffer
        sorterÅrslønnerBasertPåNyesteÅrslønn(årslønner);
        if (summerNyligeÅrslønner(3, årslønner) >= grunnbeløpVerktøy.hentTotaltGrunnbeløpForGittAntallÅr(3)) {
            return true;
        } else if (hentÅrslønnVedIndeks(årslønner, 0).hentÅrslønn() >= grunnbeløpVerktøy.hentMinimumÅrslønnForRettPåDagpenger()) {
           return true;
        }
        return false;
    }

    /**
     * Velger hva som skal være beregnings metode for dagsats ut ifra en person sine årslønner.
     * @return beregnings metode for dagsats.
     */
    public BeregningsMetode velgBeregningsMetode(List<Årslønn> årslønner) {
        //Trenger ikke å kalle på metoden flere ganger, kan heller lagre den i en variabel og bruke den senere gaga gogo
        BeregningsMetode beregningsMetode; 
        sorterÅrslønnerBasertPåNyesteÅrslønn(årslønner);
        double sisteÅrslønn = hentÅrslønnVedIndeks(årslønner, 0).hentÅrslønn();
        if (sisteÅrslønn > (summerNyligeÅrslønner(3, årslønner) / 3)) {

           beregningsMetode = BeregningsMetode.SISTE_ÅRSLØNN;
           
           if (sisteÅrslønn > grunnbeløpVerktøy.hentMaksÅrligDagpengegrunnlag()) {
               beregningsMetode = BeregningsMetode.MAKS_ÅRLIG_DAGPENGERGRUNNLAG;
           }
        } else {
            beregningsMetode = BeregningsMetode.GJENNOMSNITTET_AV_TRE_ÅR;
        }

        return beregningsMetode;
    }

    public void leggTilÅrslønn(List<Årslønn> årslønner, Årslønn årslønn) {
        if (årslønner == null) {
            throw new IllegalArgumentException("Årslønnsliste kan ikke være null");
        }

        årslønner.add(årslønn);
        sorterÅrslønnerBasertPåNyesteÅrslønn(årslønner);
    }

    /**
     * Henter årslønnen i registeret basert på dens posisjon i registeret ved gitt indeks.
     * @param indeks posisjonen til årslønnen.
     * @return årslønnen ved gitt indeks.
     */
    public Årslønn hentÅrslønnVedIndeks(List<Årslønn> årslønner, int indeks) {
        return årslønner.get(indeks);
    }

    /**
     * Summemer sammen antall årslønner basert på gitt parameter.
     * @param antallÅrÅSummere antall år med årslønner vi vil summere.
     * @return summen av årslønner.
     */
    public double summerNyligeÅrslønner(int antallÅrÅSummere, List<Årslønn> årslønner) {
        double sumAvNyligeÅrslønner = 0;
        sorterÅrslønnerBasertPåNyesteÅrslønn(årslønner);
        
        if (antallÅrÅSummere <= 0) {
            throw new IllegalArgumentException("Antall år å summere må være større enn 0");
        }

        if (antallÅrÅSummere <= årslønner.size()) {
            List<Årslønn> subÅrslønnListe = årslønner.subList(0, antallÅrÅSummere);

            for (Årslønn årslønn : subÅrslønnListe) {
                sumAvNyligeÅrslønner += årslønn.hentÅrslønn();
            }
            return sumAvNyligeÅrslønner;
        }else {
            throw new IllegalArgumentException("Antall år å summere kan ikke være større enn antall årslønner i registeret");
        }
    }

    /**
     * Sorterer registeret slik at den nyligste årslønnen er det først elementet i registeret.
     * Først blir årslønnene i registeret sortert ut at den eldstre årslønnen skal først i registeret,
     * deretter blir registeret reversert.
     */
    public void sorterÅrslønnerBasertPåNyesteÅrslønn(List<Årslønn> årslønner) {
        if (årslønner == null) {
            throw new IllegalArgumentException("Årslønnsliste kan ikke være null");
        }
        //Mutable List, metode som kan sortere listen i seg selv.
        årslønner.sort(Comparator.comparingInt(Årslønn::hentÅretForLønn).reversed());
        //Reversed lar deg bare direkte sortere listen i omvendt rekjefulge
       //Collections.reverse(this.årslønner);
    }

}
