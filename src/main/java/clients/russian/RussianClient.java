package clients.russian;

import clients.russian.data.AdjectiveGendersResult;
import clients.russian.data.CorrectionEntry;
import clients.russian.data.DeclensionResult;
import clients.russian.data.NumberSpellingResult;
import com.fasterxml.jackson.core.type.TypeReference;
import communicator.PathCommunicator;
import exceptions.MorpherException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static communicator.Communicator.METHOD_DELETE;
import static communicator.Communicator.METHOD_GET;
import static communicator.Communicator.METHOD_POST;


public class RussianClient {
    private PathCommunicator communicator;

    public RussianClient(PathCommunicator communicator) {
        this.communicator = communicator;
    }

    public DeclensionResult declension(String lemma) throws MorpherException, IOException {
        TypeReference<DeclensionResult> responseType = new TypeReference<DeclensionResult>() {
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("s", lemma);

        DeclensionResult declension = communicator.sendRequest("declension", params, METHOD_GET, responseType);
        declension.nominative = lemma;

        return declension;
    }

    public NumberSpellingResult spell(int number, String unit) throws MorpherException, IOException {
        TypeReference<NumberSpellingResult> responseType = new TypeReference<NumberSpellingResult>() {
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("n", String.valueOf(number));
        params.put("unit", unit);

        return communicator.sendRequest("spell", params, METHOD_GET, responseType);
    }

    public List<String> adjectivize(String lemma) throws MorpherException, IOException {
        TypeReference<ArrayList<String>> responseType = new TypeReference<ArrayList<String>>() {
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("s", lemma);

        return communicator.sendRequest("adjectivize", params, METHOD_GET, responseType);
    }

    public AdjectiveGendersResult adjectiveGenders(String lemma) throws MorpherException, IOException {
        TypeReference<AdjectiveGendersResult> responseType = new TypeReference<AdjectiveGendersResult>() {
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("s", lemma);

        return communicator.sendRequest("genders", params, METHOD_GET, responseType);
    }

    public void addOrUpdateUserDict(CorrectionEntry correctionEntry) throws MorpherException, IOException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("И", correctionEntry.singular.nominative);
        params.put("Р", correctionEntry.singular.genitive);
        params.put("Д", correctionEntry.singular.dative);
        params.put("В", correctionEntry.singular.accusative);
        params.put("Т", correctionEntry.singular.instrumental);
        params.put("П", correctionEntry.singular.prepositional);
        params.put("М", correctionEntry.singular.locative);

        if (correctionEntry.plural != null) {
            //Plural population
            params.put("М_И", correctionEntry.plural.nominative);
            params.put("М_Р", correctionEntry.plural.genitive);
            params.put("М_Д", correctionEntry.plural.dative);
            params.put("М_В", correctionEntry.plural.accusative);
            params.put("М_Т", correctionEntry.plural.instrumental);
            params.put("М_П", correctionEntry.plural.prepositional);
            params.put("М_М", correctionEntry.plural.locative);
        }

        communicator.sendRequest("userdict", params, METHOD_POST, null);
    }

    public List<CorrectionEntry> fetchAllFromUserDictionary() throws MorpherException, IOException {
        TypeReference<List<CorrectionEntry>> responseType = new TypeReference<List<CorrectionEntry>>() {
        };

        return communicator.sendRequest("userdict", new HashMap<String, String>(), METHOD_GET, responseType);
    }

    public boolean removeFromUserDictionary(String nominativeCorrection) throws MorpherException, IOException {
        TypeReference<Boolean> responseType = new TypeReference<Boolean>() {
        };

        Map<String, String> params = new HashMap<String, String>();
        params.put("s", nominativeCorrection);

        return communicator.sendRequest("userdict", params, METHOD_DELETE,responseType);
    }
}
