package com.example.lexify.game;

import com.example.lexify.model.Level;
import com.example.lexify.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {
    private List<Level> allLevels;
    private int currentLevelIndex = 0;        // 0-based index of the current level being played
    private int currentWordIndexInLevel;  // 0-based index of the current word within that level
    private Word currentWordToGuess;      // The actual Word object for the current word
    private static final int MAX_HINTS_PER_WORD = 3;
    private int hintsUsedForCurrentWord = 0;

    public GameManager() {
        loadGameData();
    }

    private Word.Meaning createSimpleMeaning(String pos, String def, String... syns) {
        Word.Meaning m = new Word.Meaning();
        m.setPartOfSpeech(pos);
        m.setDefinition(def);
        if (syns != null && syns.length > 0) {
            m.setSynonyms(Arrays.asList(syns));
        }
        // m.setExamples(Arrays.asList("Example usage...")); // TODO: Add examples
        return m;
    }

    private Word createWord(String wordStr, Word.Meaning... meanings) {
        Word w = new Word();
        w.setWord(wordStr.toUpperCase());
        if (meanings != null) {
            w.setMeanings(Arrays.asList(meanings));
        } else {
            w.setMeanings(new ArrayList<>());
        }
        return w;
    }

    private void loadGameData() {
        allLevels = new ArrayList<>();
        List<Word> words;

        // === Level 1: Basic 4-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("BOOK", 
            createSimpleMeaning("noun", "A written or printed work consisting of pages glued or sewn together along one side and bound in covers.", "volume", "text", "publication"),
            createSimpleMeaning("verb", "To reserve (something) for future use.", "reserve", "schedule", "arrange")));
        words.add(createWord("READ", 
            createSimpleMeaning("verb", "To look at and comprehend the meaning of (written or printed matter) by interpreting the characters or symbols of which it is composed.", "peruse", "study", "scan"),
            createSimpleMeaning("noun", "A period or an occasion of reading something.", "perusal", "review", "study")));
        words.add(createWord("WORD", 
            createSimpleMeaning("noun", "A single distinct meaningful element of speech or writing, used to form sentences and communicate ideas.", "term", "expression", "utterance")));
        words.add(createWord("QUIZ", 
            createSimpleMeaning("noun", "A test of knowledge, especially as a competition between individuals or teams as a form of entertainment.", "test", "examination", "questionnaire"),
            createSimpleMeaning("verb", "To ask questions of (someone), especially in an informal but thorough manner.", "question", "interrogate", "examine")));
        allLevels.add(new Level(1, words, "Level 1: Basic 4-letter Words"));

        // === Level 2: Nature 4-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("TREE", 
            createSimpleMeaning("noun", "A woody perennial plant with a single main stem or trunk, supporting branches and leaves.", "sapling", "timber", "plant")));
        words.add(createWord("LEAF", 
            createSimpleMeaning("noun", "A flattened structure of a plant, typically green and blade-like, that is attached to a stem directly or via a stalk.", "frond", "blade", "foliage"),
            createSimpleMeaning("verb", "To produce or grow leaves.", "sprout", "bud", "shoot")));
        words.add(createWord("SEED", 
            createSimpleMeaning("noun", "The unit of reproduction of a flowering plant, capable of developing into another such plant.", "grain", "kernel", "pit"),
            createSimpleMeaning("verb", "To plant seeds in (land) or (of a plant) to shed seeds.", "sow", "plant", "scatter")));
        words.add(createWord("SOIL", 
            createSimpleMeaning("noun", "The upper layer of earth in which plants grow, a black or dark brown material typically consisting of organic remains, clay, and rock particles.", "earth", "dirt", "ground")));
        allLevels.add(new Level(2, words, "Level 2: Nature Terms"));

        // === Level 3: Animal 4-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("BEAR", 
            createSimpleMeaning("noun", "A large, heavy mammal that walks on the soles of its feet, with thick fur and a very short tail.", "bruin", "ursine", "grizzly"),
            createSimpleMeaning("verb", "To carry or support (something).", "hold", "sustain", "transport")));
        words.add(createWord("LION", 
            createSimpleMeaning("noun", "A large cat of the genus Panthera, native to Africa, with a tawny coat and a tufted tail. The male has a heavy mane.", "feline", "predator", "beast")));
        words.add(createWord("WOLF", 
            createSimpleMeaning("noun", "A wild carnivorous mammal of the dog family, living and hunting in packs.", "canine", "predator", "hunter")));
        words.add(createWord("DEER", 
            createSimpleMeaning("noun", "A hoofed grazing or browsing animal, with branched bony antlers that are shed annually and typically borne only by the male.", "stag", "doe", "buck")));
        allLevels.add(new Level(3, words, "Level 3: Animal Kingdom"));

        // === Level 4: Weather 4-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("RAIN", 
            createSimpleMeaning("noun", "The condensed moisture of the atmosphere falling visibly in separate drops.", "precipitation", "shower", "drizzle"),
            createSimpleMeaning("verb", "To fall in drops of water from the clouds.", "pour", "shower", "drip")));
        words.add(createWord("SNOW", 
            createSimpleMeaning("noun", "Atmospheric water vapor frozen into ice crystals and falling in light white flakes.", "powder", "sleet", "frost"),
            createSimpleMeaning("verb", "To fall as snow.", "flurry", "drift", "blanket")));
        words.add(createWord("WIND", 
            createSimpleMeaning("noun", "The natural movement of the air, especially in the form of a current of air blowing from a particular direction.", "breeze", "gust", "draft")));
        words.add(createWord("HAIL", 
            createSimpleMeaning("noun", "Pellets of frozen rain that fall in showers from cumulonimbus clouds.", "ice", "sleet", "precipitation"),
            createSimpleMeaning("verb", "To call out to (someone) to attract attention.", "greet", "address", "salute")));
        allLevels.add(new Level(4, words, "Level 4: Weather Phenomena"));

        // === Level 5: Food 4-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CAKE", 
            createSimpleMeaning("noun", "A sweet baked food made from a mixture of flour, eggs, sugar, and other ingredients.", "pastry", "dessert", "treat")));
        words.add(createWord("MEAT", 
            createSimpleMeaning("noun", "The flesh of an animal (especially a mammal) as food.", "flesh", "protein", "fare")));
        words.add(createWord("FISH", 
            createSimpleMeaning("noun", "A limbless cold-blooded vertebrate animal with gills and fins living wholly in water.", "seafood", "catch", "marine"),
            createSimpleMeaning("verb", "To catch or try to catch fish, typically by using a net or hook and line.", "angle", "trawl", "cast")));
        words.add(createWord("SOUP", 
            createSimpleMeaning("noun", "A liquid dish, typically savory and made by boiling meat, fish, or vegetables in stock or water.", "broth", "bisque", "stew")));
        allLevels.add(new Level(5, words, "Level 5: Food & Cuisine"));

        // === Level 6: Body 5-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("HEART", 
            createSimpleMeaning("noun", "A hollow muscular organ that pumps blood through the circulatory system by rhythmic contraction and dilation.", "core", "center", "essence")));
        words.add(createWord("BRAIN", 
            createSimpleMeaning("noun", "An organ of soft nervous tissue contained in the skull of vertebrates, functioning as the coordinating center of sensation and intellectual and nervous activity.", "mind", "intellect", "psyche")));
        words.add(createWord("BLOOD", 
            createSimpleMeaning("noun", "The red liquid that circulates in the arteries and veins of humans and other vertebrate animals, carrying oxygen to and carbon dioxide from the tissues of the body.", "plasma", "fluid", "gore")));
        words.add(createWord("SPINE", 
            createSimpleMeaning("noun", "The series of vertebrae extending from the skull to the pelvis; the backbone.", "backbone", "column", "vertebrae")));
        allLevels.add(new Level(6, words, "Level 6: Human Anatomy"));

        // === Level 7: Space 5-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("EARTH", 
            createSimpleMeaning("noun", "The planet on which we live; the world. The third planet from the sun in the solar system.", "world", "globe", "planet")));
        words.add(createWord("SPACE", 
            createSimpleMeaning("noun", "The unlimited or incalculably great three-dimensional realm or expanse in which all material objects are located and all events occur.", "cosmos", "universe", "void")));
        words.add(createWord("STARS", 
            createSimpleMeaning("noun", "A fixed luminous point in the night sky which is a large, remote incandescent body like the sun.", "suns", "celestial bodies", "luminaries")));
        words.add(createWord("COMET", 
            createSimpleMeaning("noun", "A celestial object consisting of a nucleus of ice and dust and, when near the sun, a 'tail' of gas and dust particles pointing away from the sun.", "meteor", "asteroid", "bolide")));
        allLevels.add(new Level(7, words, "Level 7: Space Exploration"));

        // === Level 8: Music 5-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("PIANO", 
            createSimpleMeaning("noun", "A large keyboard musical instrument with a wooden case enclosing a soundboard and metal strings, which are struck by hammers when the keys are depressed.", "keyboard", "grand", "upright")));
        words.add(createWord("DRUMS", 
            createSimpleMeaning("noun", "A percussion instrument consisting of a hollow shell or cylinder with a drumhead stretched over one or both ends that is beaten with sticks or the hands.", "percussion", "tympani", "skins")));
        words.add(createWord("FLUTE", 
            createSimpleMeaning("noun", "A musical wind instrument consisting of a tube with a series of fingerholes or keys, in which the sound is produced by the flow of air across an opening.", "pipe", "recorder", "fife")));
        words.add(createWord("MUSIC", 
            createSimpleMeaning("noun", "Vocal or instrumental sounds (or both) combined in such a way as to produce beauty of form, harmony, and expression of emotion.", "melody", "harmony", "sound")));
        allLevels.add(new Level(8, words, "Level 8: Musical Instruments"));

        // Continue with more levels...
        // === Level 9: Science 5-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ATOMS", 
            createSimpleMeaning("noun", "The basic unit of a chemical element, consisting of a nucleus containing protons and neutrons, surrounded by electrons.", "particles", "elements", "units")));
        words.add(createWord("FORCE", 
            createSimpleMeaning("noun", "Any interaction that, when unopposed, will change the motion of an object.", "power", "energy", "strength"),
            createSimpleMeaning("verb", "To make someone do something against their will.", "compel", "coerce", "drive")));
        words.add(createWord("LIGHT", 
            createSimpleMeaning("noun", "The natural agent that stimulates sight and makes things visible; electromagnetic radiation.", "illumination", "brightness", "radiance"),
            createSimpleMeaning("adjective", "Having a relatively small amount of weight.", "lightweight", "slight", "airy")));
        words.add(createWord("SOUND", 
            createSimpleMeaning("noun", "Vibrations that travel through the air or another medium and can be heard when they reach a person's ear.", "noise", "audio", "tone"),
            createSimpleMeaning("adjective", "In good condition; not damaged, injured, or diseased.", "healthy", "solid", "stable")));
        allLevels.add(new Level(9, words, "Level 9: Scientific Concepts"));

        // === Level 10: Emotions 5-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("HAPPY", 
            createSimpleMeaning("adjective", "Feeling or showing pleasure or contentment.", "joyful", "cheerful", "merry")));
        words.add(createWord("ANGRY", 
            createSimpleMeaning("adjective", "Having strong feelings of annoyance, displeasure, or hostility.", "furious", "irate", "enraged")));
        words.add(createWord("PEACE", 
            createSimpleMeaning("noun", "Freedom from disturbance; tranquility.", "calm", "quiet", "serenity")));
        words.add(createWord("SMILE", 
            createSimpleMeaning("verb", "Form one's features into a pleased, kind, or amused expression.", "beam", "grin", "laugh"),
            createSimpleMeaning("noun", "A pleased, kind, or amused facial expression.", "grin", "beam", "smirk")));
        allLevels.add(new Level(10, words, "Level 10: Human Emotions"));

        // === Level 11: Technology 6-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("LAPTOP", 
            createSimpleMeaning("noun", "A portable computer, small enough to use on one's lap, that integrates screen, keyboard, and pointing device.", "computer", "notebook", "device")));
        words.add(createWord("CAMERA", 
            createSimpleMeaning("noun", "An optical instrument for recording or capturing images, which may be stored locally, transmitted to another location, or both.", "imager", "recorder", "shooter")));
        words.add(createWord("SCREEN", 
            createSimpleMeaning("noun", "The display part of an electronic device, typically comprising the area on which images, text, or video is displayed.", "display", "monitor", "panel"),
            createSimpleMeaning("verb", "To show (a film or video) or to test (someone or something) by examining them.", "display", "project", "check")));
        words.add(createWord("ROUTER", 
            createSimpleMeaning("noun", "A networking device that forwards data packets between computer networks, directing traffic between them.", "gateway", "switch", "hub")));
        allLevels.add(new Level(11, words, "Level 11: Modern Technology"));

        // === Level 12: Plants 6-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("FLOWER", 
            createSimpleMeaning("noun", "The reproductive structure found in flowering plants, typically consisting of petals, stamens, and carpels.", "bloom", "blossom", "floret"),
            createSimpleMeaning("verb", "To produce flowers; to blossom.", "bloom", "blossom", "flourish")));
        words.add(createWord("GARDEN", 
            createSimpleMeaning("noun", "A piece of ground devoted to growing flowers, fruits, vegetables, or other plants.", "yard", "patch", "plot"),
            createSimpleMeaning("verb", "To cultivate or work in a garden.", "plant", "grow", "tend")));
        words.add(createWord("FOREST", 
            createSimpleMeaning("noun", "A large area covered chiefly with trees and undergrowth.", "woodland", "grove", "woods")));
        words.add(createWord("BRANCH", 
            createSimpleMeaning("noun", "A part of a tree that grows out from the trunk or from a bough.", "limb", "stem", "offshoot"),
            createSimpleMeaning("verb", "To divide into subdivisions or separate parts.", "split", "fork", "diverge")));
        allLevels.add(new Level(12, words, "Level 12: Plant Life"));

        // === Level 13: Ocean 6-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("MARINE", 
            createSimpleMeaning("adjective", "Relating to or found in the sea.", "oceanic", "nautical", "aquatic"),
            createSimpleMeaning("noun", "A member of a nation's sea forces.", "sailor", "seaman", "seafarer")));
        words.add(createWord("WHALE", 
            createSimpleMeaning("noun", "A very large marine mammal with a streamlined hairless body, a horizontal tail fin, and a blowhole for breathing.", "cetacean", "leviathan", "rorqual")));
        words.add(createWord("CORAL", 
            createSimpleMeaning("noun", "A hard calcareous substance secreted by marine polyps for support and habitation.", "reef", "atoll", "polyp")));
        words.add(createWord("WAVES", 
            createSimpleMeaning("noun", "A disturbance on the surface of a liquid body, as the sea, in which the mass of water forms a ridge or swell that moves along while the particles that compose it merely oscillate.", "breakers", "swells", "surf"),
            createSimpleMeaning("verb", "To move one's hand to and fro in greeting or farewell.", "gesture", "signal", "flutter")));
        allLevels.add(new Level(13, words, "Level 13: Ocean Life"));

        // === Level 14: Sports 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ATHLETE", 
            createSimpleMeaning("noun", "A person who is proficient in sports and other forms of physical exercise.", "player", "sportsman", "competitor")));
        words.add(createWord("RUNNING", 
            createSimpleMeaning("noun", "The action or movement of a runner.", "jogging", "sprinting", "dashing"),
            createSimpleMeaning("adjective", "Moving at a speed faster than a walk.", "moving", "active", "ongoing")));
        words.add(createWord("CYCLING", 
            createSimpleMeaning("noun", "The sport or activity of riding a bicycle.", "biking", "pedaling", "wheeling"),
            createSimpleMeaning("verb", "Ride a bicycle.", "bike", "pedal", "wheel")));
        words.add(createWord("SKATING", 
            createSimpleMeaning("noun", "The sport or pastime of gliding on ice or rollers wearing skates.", "gliding", "rolling", "sliding")));
        allLevels.add(new Level(14, words, "Level 14: Sports & Exercise"));

        // === Level 15: Science 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("PHYSICS", 
            createSimpleMeaning("noun", "The branch of science concerned with the nature and properties of matter and energy.", "science", "mechanics", "dynamics")));
        words.add(createWord("BIOLOGY", 
            createSimpleMeaning("noun", "The study of living organisms, including their structure, function, growth, origin, evolution, and distribution.", "life science", "bioscience", "natural science")));
        words.add(createWord("GEOLOGY", 
            createSimpleMeaning("noun", "The science that deals with the earth's physical structure and substance, its history, and the processes that act on it.", "earth science", "geoscience", "mineralogy")));
        words.add(createWord("CLIMATE", 
            createSimpleMeaning("noun", "The weather conditions prevailing in an area in general or over a long period.", "weather", "atmosphere", "conditions")));
        allLevels.add(new Level(15, words, "Level 15: Scientific Fields"));

        // === Level 16: Culture 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CULTURE", 
            createSimpleMeaning("noun", "The arts and other manifestations of human intellectual achievement regarded collectively.", "civilization", "society", "customs"),
            createSimpleMeaning("verb", "Maintain (tissue cells, bacteria, etc.) in conditions suitable for growth.", "grow", "cultivate", "develop")));
        words.add(createWord("HISTORY", 
            createSimpleMeaning("noun", "The study of past events, particularly in human affairs.", "chronicle", "record", "annals")));
        words.add(createWord("SOCIETY", 
            createSimpleMeaning("noun", "The aggregate of people living together in a more or less ordered community.", "community", "public", "population")));
        words.add(createWord("CUSTOMS", 
            createSimpleMeaning("noun", "Traditional and widely accepted ways of behaving or doing things in a society or culture.", "traditions", "practices", "habits")));
        allLevels.add(new Level(16, words, "Level 16: Cultural Concepts"));

        // === Level 17: Medicine 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("THERAPY", 
            createSimpleMeaning("noun", "Treatment intended to relieve or heal a disorder.", "treatment", "healing", "remedy")));
        words.add(createWord("VACCINE", 
            createSimpleMeaning("noun", "A substance used to stimulate the production of antibodies and provide immunity against one or several diseases.", "immunization", "inoculation", "shot")));
        words.add(createWord("SURGERY", 
            createSimpleMeaning("noun", "The treatment of injuries or disorders of the body by incision or manipulation.", "operation", "procedure", "intervention")));
        words.add(createWord("HEALING", 
            createSimpleMeaning("noun", "The process of becoming or making someone or something healthy again.", "recovery", "cure", "mending"),
            createSimpleMeaning("adjective", "Having the ability to cure or restore to health.", "curative", "therapeutic", "medicinal")));
        allLevels.add(new Level(17, words, "Level 17: Medical Terms"));

        // === Level 18: Environment 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ECOLOGY", 
            createSimpleMeaning("noun", "The branch of biology that deals with the relations of organisms to one another and to their physical surroundings.", "ecosystem", "environment", "biosphere")));
        words.add(createWord("WEATHER", 
            createSimpleMeaning("noun", "The state of the atmosphere at a place and time as regards heat, dryness, sunshine, wind, rain, etc.", "climate", "conditions", "elements"),
            createSimpleMeaning("verb", "To wear away or change under the action of the weather.", "erode", "decay", "age")));
        words.add(createWord("HABITAT", 
            createSimpleMeaning("noun", "The natural home or environment of an animal, plant, or other organism.", "environment", "ecosystem", "biotope")));
        words.add(createWord("NATURAL", 
            createSimpleMeaning("adjective", "Existing in or caused by nature; not made or caused by humankind.", "organic", "native", "wild")));
        allLevels.add(new Level(18, words, "Level 18: Environmental Science"));

        // === Level 19: Arts 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("PAINTING", 
            createSimpleMeaning("noun", "The process or art of using paint, in a picture, as a protective coating, or as decoration.", "artwork", "drawing", "canvas"),
            createSimpleMeaning("verb", "Cover the surface of something with paint.", "coat", "color", "decorate")));
        words.add(createWord("DRAWING", 
            createSimpleMeaning("noun", "A picture or diagram made with a pencil, pen, or crayon rather than paint.", "sketch", "illustration", "artwork"),
            createSimpleMeaning("verb", "Produce a picture or diagram by making lines and marks on paper.", "sketch", "depict", "trace")));
        words.add(createWord("SCULPT", 
            createSimpleMeaning("verb", "Create or represent something by carving, casting, or other shaping techniques.", "carve", "shape", "mold"),
            createSimpleMeaning("noun", "A three-dimensional work of art.", "statue", "carving", "figure")));
        words.add(createWord("THEATRE", 
            createSimpleMeaning("noun", "A building or outdoor area for dramatic performances.", "playhouse", "stage", "arena")));
        allLevels.add(new Level(19, words, "Level 19: Arts & Performance"));

        // === Level 20: Literature 7-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("WRITING", 
            createSimpleMeaning("noun", "The activity or skill of marking coherent words on paper and composing text.", "composition", "penmanship", "script"),
            createSimpleMeaning("verb", "Mark coherent words on paper or compose text.", "compose", "record", "draft")));
        words.add(createWord("CHAPTER", 
            createSimpleMeaning("noun", "A main division of a book, typically with a number or title.", "section", "part", "division")));
        words.add(createWord("LIBRARY", 
            createSimpleMeaning("noun", "A building or room containing collections of books, periodicals, and sometimes films and recorded music for use or borrowing.", "archive", "collection", "repository")));
        words.add(createWord("STORIES", 
            createSimpleMeaning("noun", "Accounts of imaginary or real people and events told for entertainment.", "tales", "narratives", "accounts")));
        allLevels.add(new Level(20, words, "Level 20: Literary Terms"));

        // === Level 21: Chemistry 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CHEMICAL", 
            createSimpleMeaning("noun", "A substance produced by or used in a chemical process.", "compound", "substance", "element"),
            createSimpleMeaning("adjective", "Relating to chemistry or the interactions of substances.", "molecular", "synthetic", "processed")));
        words.add(createWord("REACTION", 
            createSimpleMeaning("noun", "A process in which substances act mutually on each other and are changed into different substances, or one substance changes into other substances.", "response", "change", "transformation")));
        words.add(createWord("MOLECULE", 
            createSimpleMeaning("noun", "The smallest particle of a substance that retains all the properties of that substance and is composed of one or more atoms.", "particle", "unit", "entity")));
        words.add(createWord("SOLUTION", 
            createSimpleMeaning("noun", "A liquid mixture in which the minor component (the solute) is uniformly distributed within the major component (the solvent).", "mixture", "blend", "compound")));
        allLevels.add(new Level(21, words, "Level 21: Chemistry Concepts"));

        // === Level 22: Astronomy 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("UNIVERSE", 
            createSimpleMeaning("noun", "All existing matter, space, and energy as a whole.", "cosmos", "creation", "macrocosm")));
        words.add(createWord("ASTEROID", 
            createSimpleMeaning("noun", "A small rocky body orbiting the sun, especially in the region between Mars and Jupiter.", "planetoid", "meteoroid", "space rock")));
        words.add(createWord("GALAXIES", 
            createSimpleMeaning("noun", "Large systems of stars held together by mutual gravitation and isolated from similar systems by vast regions of space.", "nebulae", "constellations", "star systems")));
        words.add(createWord("TELESCOPE", 
            createSimpleMeaning("noun", "An optical instrument designed to make distant objects appear nearer, containing an arrangement of lenses, or of curved mirrors and lenses.", "scope", "spyglass", "glass")));
        allLevels.add(new Level(22, words, "Level 22: Space Science"));

        // === Level 23: Geography 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("MOUNTAIN", 
            createSimpleMeaning("noun", "A large natural elevation of the earth's surface rising abruptly from the surrounding level.", "peak", "summit", "elevation")));
        words.add(createWord("CONTINENT", 
            createSimpleMeaning("noun", "Any of the world's main continuous expanses of land (Africa, Antarctica, Asia, Australia, Europe, North America, South America).", "landmass", "mainland", "terra firma")));
        words.add(createWord("LATITUDE", 
            createSimpleMeaning("noun", "The angular distance of a place north or south of the earth's equator, measured in degrees.", "parallel", "coordinates", "position")));
        words.add(createWord("PLATEAU", 
            createSimpleMeaning("noun", "An area of relatively level high ground; a tableland.", "upland", "mesa", "highland")));
        allLevels.add(new Level(23, words, "Level 23: Earth Features"));

        // === Level 24: Biology 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ORGANISM", 
            createSimpleMeaning("noun", "An individual animal, plant, or single-celled life form.", "creature", "being", "life form")));
        words.add(createWord("GENETICS", 
            createSimpleMeaning("noun", "The study of heredity and the variation of inherited characteristics.", "inheritance", "heredity", "genes")));
        words.add(createWord("BACTERIA", 
            createSimpleMeaning("noun", "Microscopic, single-celled organisms that can exist either as independent organisms or as parasites.", "microbes", "germs", "microorganisms")));
        words.add(createWord("CELLULAR", 
            createSimpleMeaning("adjective", "Relating to or consisting of cells.", "cellular", "microscopic", "biological")));
        allLevels.add(new Level(24, words, "Level 24: Life Science"));

        // === Level 25: Technology 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("COMPUTER", 
            createSimpleMeaning("noun", "An electronic device for storing and processing data according to instructions given to it in a variable program.", "processor", "machine", "system")));
        words.add(createWord("SOFTWARE", 
            createSimpleMeaning("noun", "The programs and other operating information used by a computer.", "programs", "applications", "systems")));
        words.add(createWord("INTERNET", 
            createSimpleMeaning("noun", "A global computer network providing a variety of information and communication facilities.", "web", "cyberspace", "network")));
        words.add(createWord("WIRELESS", 
            createSimpleMeaning("adjective", "Using radio, microwaves, etc. (rather than wires or cables) to transmit signals.", "radio", "cordless", "remote")));
        allLevels.add(new Level(25, words, "Level 25: Digital World"));

        // === Level 26: Psychology 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("BEHAVIOR", 
            createSimpleMeaning("noun", "The way in which one acts or conducts oneself, especially toward others.", "conduct", "actions", "demeanor")));
        words.add(createWord("THINKING", 
            createSimpleMeaning("noun", "The process of using one's mind to consider or reason about something.", "thought", "reasoning", "cognition"),
            createSimpleMeaning("adjective", "Using thought or rational judgment; intelligent.", "thoughtful", "rational", "logical")));
        words.add(createWord("LEARNING", 
            createSimpleMeaning("noun", "The acquisition of knowledge or skills through experience, study, or being taught.", "education", "study", "training")));
        words.add(createWord("MEMORY", 
            createSimpleMeaning("noun", "The faculty by which the mind stores and remembers information.", "recall", "retention", "remembrance")));
        allLevels.add(new Level(26, words, "Level 26: Mind & Behavior"));

        // === Level 27: Literature 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("LANGUAGE", 
            createSimpleMeaning("noun", "The method of human communication, either spoken or written, consisting of the use of words in a structured and conventional way.", "speech", "tongue", "dialect")));
        words.add(createWord("METAPHOR", 
            createSimpleMeaning("noun", "A figure of speech in which a word or phrase is applied to an object or action to which it is not literally applicable.", "analogy", "symbol", "image")));
        words.add(createWord("NARRATIVE", 
            createSimpleMeaning("noun", "A spoken or written account of connected events; a story.", "story", "tale", "account"),
            createSimpleMeaning("adjective", "In the form of or concerned with narration.", "descriptive", "storytelling", "sequential")));
        words.add(createWord("POETRY", 
            createSimpleMeaning("noun", "Literary work in which special intensity is given to the expression of feelings and ideas by the use of distinctive style and rhythm.", "verse", "rhyme", "lyrics")));
        allLevels.add(new Level(27, words, "Level 27: Literary Terms"));

        // === Level 28: Medicine 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("MEDICINE", 
            createSimpleMeaning("noun", "The science or practice of the diagnosis, treatment, and prevention of disease.", "treatment", "remedy", "cure")));
        words.add(createWord("DIAGNOSIS", 
            createSimpleMeaning("noun", "The identification of the nature of an illness or other problem by examination of the symptoms.", "analysis", "assessment", "evaluation")));
        words.add(createWord("PATHOLOGY", 
            createSimpleMeaning("noun", "The science of the causes and effects of diseases.", "medicine", "biology", "science")));
        words.add(createWord("PHARMACY", 
            createSimpleMeaning("noun", "The science or practice of the preparation and dispensing of medicinal drugs.", "chemist", "drugstore", "dispensary")));
        allLevels.add(new Level(28, words, "Level 28: Medical Science"));

        // === Level 29: Economics 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("BUSINESS", 
            createSimpleMeaning("noun", "The practice of making one's living by engaging in commerce.", "trade", "commerce", "enterprise")));
        words.add(createWord("ECONOMY", 
            createSimpleMeaning("noun", "The state of a country or region in terms of the production and consumption of goods and services and the supply of money.", "market", "commerce", "trade")));
        words.add(createWord("FINANCE", 
            createSimpleMeaning("noun", "The management of large amounts of money, especially by governments or large companies.", "banking", "economics", "money")));
        words.add(createWord("INDUSTRY", 
            createSimpleMeaning("noun", "Economic activity concerned with the processing of raw materials and manufacture of goods in factories.", "manufacturing", "production", "commerce")));
        allLevels.add(new Level(29, words, "Level 29: Business & Economics"));

        // === Level 30: Philosophy 8-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("THINKING", 
            createSimpleMeaning("noun", "The process of using one's mind to consider or reason about something.", "reasoning", "contemplation", "thought"),
            createSimpleMeaning("adjective", "Using thought or rational judgment; intelligent.", "rational", "logical", "reasoned")));
        words.add(createWord("WISDOM", 
            createSimpleMeaning("noun", "The quality of having experience, knowledge, and good judgment.", "knowledge", "insight", "understanding")));
        words.add(createWord("MORALITY", 
            createSimpleMeaning("noun", "Principles concerning the distinction between right and wrong or good and bad behavior.", "ethics", "principles", "values")));
        words.add(createWord("REALITY", 
            createSimpleMeaning("noun", "The state of things as they actually exist, as opposed to an idealistic or notional idea of them.", "actuality", "truth", "fact")));
        allLevels.add(new Level(30, words, "Level 30: Philosophical Concepts"));

        // === Level 31: Advanced Science 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CHEMISTRY", 
            createSimpleMeaning("noun", "The scientific study of the properties, composition, and structure of substances and the changes they undergo.", "science", "elements", "compounds")));
        words.add(createWord("PHYSICIST", 
            createSimpleMeaning("noun", "A scientist who specializes in physics, particularly in the interactions between energy and matter.", "scientist", "researcher", "scholar")));
        words.add(createWord("MOLECULAR", 
            createSimpleMeaning("adjective", "Relating to or consisting of molecules, the smallest particles of a substance that have all the properties of that substance.", "atomic", "chemical", "microscopic")));
        words.add(createWord("RADIATION", 
            createSimpleMeaning("noun", "The emission of energy as electromagnetic waves or as moving subatomic particles.", "emission", "transmission", "rays")));
        allLevels.add(new Level(31, words, "Level 31: Advanced Science"));

        // === Level 32: Mathematics 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("GEOMETRY", 
            createSimpleMeaning("noun", "The branch of mathematics concerned with the properties and relations of points, lines, surfaces, solids, and higher dimensional analogs.", "shapes", "mathematics", "measurement")));
        words.add(createWord("CALCULATE", 
            createSimpleMeaning("verb", "To determine by reasoning, experience, or calculation; to reckon or compute.", "compute", "reckon", "determine")));
        words.add(createWord("EQUATIONS", 
            createSimpleMeaning("noun", "Mathematical statements that assert the equality of two expressions.", "formulas", "expressions", "statements")));
        words.add(createWord("ALGEBRAIC", 
            createSimpleMeaning("adjective", "Using letters and other symbols to represent numbers and quantities in formulae and equations.", "mathematical", "symbolic", "formulaic")));
        allLevels.add(new Level(32, words, "Level 32: Mathematical Terms"));

        // === Level 33: Medicine 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("TREATMENT", 
            createSimpleMeaning("noun", "Medical care given to a patient for an illness or injury.", "therapy", "medication", "remedy")));
        words.add(createWord("PHYSICIAN", 
            createSimpleMeaning("noun", "A person qualified to practice medicine; a medical doctor.", "doctor", "practitioner", "medic")));
        words.add(createWord("INFECTION", 
            createSimpleMeaning("noun", "The invasion and multiplication of microorganisms in body tissues.", "disease", "contamination", "contagion")));
        words.add(createWord("ANTIBODY", 
            createSimpleMeaning("noun", "A blood protein produced in response to and counteracting a specific antigen.", "immunoglobulin", "protein", "defense")));
        allLevels.add(new Level(33, words, "Level 33: Medical Science"));

        // === Level 34: Technology 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("INTERFACE", 
            createSimpleMeaning("noun", "A point where two systems, subjects, organizations, etc. meet and interact.", "connection", "boundary", "junction"),
            createSimpleMeaning("verb", "To interact with another system, person, or organization.", "connect", "communicate", "interact")));
        words.add(createWord("DATABASE", 
            createSimpleMeaning("noun", "A structured set of data held in a computer, especially one that is accessible in various ways.", "repository", "records", "information")));
        words.add(createWord("PROCESSOR", 
            createSimpleMeaning("noun", "A computer component that interprets and executes instructions.", "CPU", "chip", "unit")));
        words.add(createWord("ALGORITHM", 
            createSimpleMeaning("noun", "A process or set of rules to be followed in calculations or other problem-solving operations.", "procedure", "method", "process")));
        allLevels.add(new Level(34, words, "Level 34: Computer Science"));

        // === Level 35: Psychology 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("COGNITIVE", 
            createSimpleMeaning("adjective", "Relating to cognition; concerned with the act or process of knowing, perceiving, etc.", "mental", "intellectual", "cerebral")));
        words.add(createWord("EMOTIONAL", 
            createSimpleMeaning("adjective", "Relating to a person's emotions; characterized by intense feeling.", "passionate", "sensitive", "expressive")));
        words.add(createWord("BEHAVIOR", 
            createSimpleMeaning("noun", "The way in which one acts or conducts oneself, especially toward others.", "conduct", "actions", "demeanor")));
        words.add(createWord("CONSCIOUS", 
            createSimpleMeaning("adjective", "Aware of and responding to one's surroundings; awake.", "aware", "alert", "mindful")));
        allLevels.add(new Level(35, words, "Level 35: Psychology Terms"));

        // === Level 36: Literature 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("METAPHORS", 
            createSimpleMeaning("noun", "Figures of speech in which a word or phrase is applied to an object or action to which it is not literally applicable.", "analogies", "symbols", "imagery")));
        words.add(createWord("NARRATIVE", 
            createSimpleMeaning("noun", "A spoken or written account of connected events; a story.", "account", "chronicle", "tale")));
        words.add(createWord("LITERATURE", 
            createSimpleMeaning("noun", "Written works, especially those considered of superior or lasting artistic merit.", "writings", "texts", "books")));
        words.add(createWord("CHARACTER", 
            createSimpleMeaning("noun", "A person in a novel, play, or movie.", "persona", "figure", "role"),
            createSimpleMeaning("noun", "The mental and moral qualities distinctive to an individual.", "personality", "nature", "disposition")));
        allLevels.add(new Level(36, words, "Level 36: Literary Elements"));

        // === Level 37: Philosophy 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("EXISTENCE", 
            createSimpleMeaning("noun", "The fact or state of living or having objective reality.", "being", "presence", "reality")));
        words.add(createWord("KNOWLEDGE", 
            createSimpleMeaning("noun", "Facts, information, and skills acquired through experience or education.", "understanding", "learning", "wisdom")));
        words.add(createWord("METAPHYSIC", 
            createSimpleMeaning("noun", "The branch of philosophy that deals with the first principles of things.", "ontology", "philosophy", "theory")));
        words.add(createWord("REASONING", 
            createSimpleMeaning("noun", "The action of thinking about something in a logical, sensible way.", "logic", "thought", "rationality")));
        allLevels.add(new Level(37, words, "Level 37: Philosophical Terms"));

        // === Level 38: Geography 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CONTINENT", 
            createSimpleMeaning("noun", "Any of the world's main continuous expanses of land.", "landmass", "mainland", "terra firma")));
        words.add(createWord("LONGITUDE", 
            createSimpleMeaning("noun", "The angular distance of a place east or west of the Greenwich meridian.", "coordinates", "position", "location")));
        words.add(createWord("PENINSULA", 
            createSimpleMeaning("noun", "A piece of land almost surrounded by water or projecting out into a body of water.", "cape", "promontory", "point")));
        words.add(createWord("LANDSCAPE", 
            createSimpleMeaning("noun", "All the visible features of an area of countryside or land.", "terrain", "scenery", "topography"),
            createSimpleMeaning("verb", "To improve the aesthetic appearance of a piece of land by changing its contours, adding ornamental features, or planting trees and shrubs.", "garden", "cultivate", "design")));
        allLevels.add(new Level(38, words, "Level 38: Geographic Features"));

        // === Level 39: Economics 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ECONOMICS", 
            createSimpleMeaning("noun", "The branch of knowledge concerned with the production, consumption, and transfer of wealth.", "finance", "commerce", "business")));
        words.add(createWord("FINANCIAL", 
            createSimpleMeaning("adjective", "Relating to money management or monetary matters.", "monetary", "fiscal", "economic")));
        words.add(createWord("MARKETING", 
            createSimpleMeaning("noun", "The action or business of promoting and selling products or services.", "advertising", "promotion", "sales")));
        words.add(createWord("COMMERCE", 
            createSimpleMeaning("noun", "The activity of buying and selling, especially on a large scale.", "trade", "business", "exchange")));
        allLevels.add(new Level(39, words, "Level 39: Business Terms"));

        // === Level 40: Sociology 9-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("SOCIOLOGY", 
            createSimpleMeaning("noun", "The study of the development, structure, and functioning of human society.", "social science", "social studies", "anthropology")));
        words.add(createWord("COMMUNITY", 
            createSimpleMeaning("noun", "A group of people living in the same place or having a particular characteristic in common.", "society", "population", "collective")));
        words.add(createWord("TRADITION", 
            createSimpleMeaning("noun", "The transmission of customs or beliefs from generation to generation.", "custom", "practice", "convention")));
        words.add(createWord("CULTURAL", 
            createSimpleMeaning("adjective", "Relating to the ideas, customs, and social behavior of a society.", "social", "ethnic", "traditional")));
        allLevels.add(new Level(40, words, "Level 40: Social Science"));

        // === Level 41: Advanced Science 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("TECHNOLOGY", 
            createSimpleMeaning("noun", "The application of scientific knowledge for practical purposes, especially in industry.", "engineering", "innovation", "advancement")));
        words.add(createWord("EXPERIMENT", 
            createSimpleMeaning("noun", "A scientific procedure undertaken to make a discovery, test a hypothesis, or demonstrate a known fact.", "test", "trial", "research"),
            createSimpleMeaning("verb", "Try out new concepts or ways of doing things.", "test", "try", "investigate")));
        words.add(createWord("HYPOTHESIS", 
            createSimpleMeaning("noun", "A supposition or proposed explanation made on the basis of limited evidence as a starting point for further investigation.", "theory", "proposition", "conjecture")));
        words.add(createWord("SCIENTIFIC", 
            createSimpleMeaning("adjective", "Based on or characterized by the methods and principles of science.", "empirical", "systematic", "methodical")));
        allLevels.add(new Level(41, words, "Level 41: Scientific Method"));

        // === Level 42: Mathematics 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("ARITHMETIC", 
            createSimpleMeaning("noun", "The branch of mathematics dealing with the properties and manipulation of numbers.", "calculation", "computation", "mathematics")));
        words.add(createWord("STATISTICS", 
            createSimpleMeaning("noun", "The practice or science of collecting and analyzing numerical data in large quantities.", "data", "figures", "numbers")));
        words.add(createWord("CALCULATOR", 
            createSimpleMeaning("noun", "An electronic device used for making mathematical calculations.", "computer", "processor", "device")));
        words.add(createWord("POLYNOMIAL", 
            createSimpleMeaning("noun", "An expression of more than two algebraic terms, especially the sum of several terms that contain different powers of the same variable.", "expression", "equation", "formula")));
        allLevels.add(new Level(42, words, "Level 42: Advanced Math"));

        // === Level 43: Biology 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("CHROMOSOME", 
            createSimpleMeaning("noun", "A thread-like structure of nucleic acids and protein found in the nucleus of most living cells, carrying genetic information in the form of genes.", "gene", "DNA", "chromatin")));
        words.add(createWord("BIOLOGICAL", 
            createSimpleMeaning("adjective", "Relating to biology or living organisms.", "natural", "organic", "living")));
        words.add(createWord("METABOLISM", 
            createSimpleMeaning("noun", "The chemical processes that occur within a living organism in order to maintain life.", "digestion", "processing", "breakdown")));
        words.add(createWord("ORGANELLES", 
            createSimpleMeaning("noun", "Specialized structures within a cell that perform specific functions.", "components", "structures", "parts")));
        allLevels.add(new Level(43, words, "Level 43: Cell Biology"));

        // === Level 44: Medicine 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("DIAGNOSTIC", 
            createSimpleMeaning("adjective", "Concerned with the diagnosis of illness or other problems.", "analytical", "symptomatic", "clinical")));
        words.add(createWord("MEDICATION", 
            createSimpleMeaning("noun", "A substance used for medical treatment, especially a medicine or drug.", "medicine", "treatment", "remedy")));
        words.add(createWord("PSYCHOLOGY", 
            createSimpleMeaning("noun", "The scientific study of the human mind and its functions, especially those affecting behavior.", "mind", "behavior", "cognition")));
        words.add(createWord("PHYSIOLOGY", 
            createSimpleMeaning("noun", "The branch of biology that deals with the normal functions of living organisms and their parts.", "functioning", "operation", "workings")));
        allLevels.add(new Level(44, words, "Level 44: Medical Science"));

        // === Level 45: Technology 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("PROCESSING", 
            createSimpleMeaning("noun", "A series of actions or steps taken in order to achieve a particular end.", "handling", "treatment", "computation"),
            createSimpleMeaning("verb", "Perform a series of mechanical or chemical operations on something in order to change or preserve it.", "handle", "treat", "prepare")));
        words.add(createWord("NETWORKING", 
            createSimpleMeaning("noun", "The linking of computers to allow them to operate interactively.", "connection", "communication", "interface"),
            createSimpleMeaning("verb", "Connect as or operate with a network.", "link", "connect", "interface")));
        words.add(createWord("ENCRYPTION", 
            createSimpleMeaning("noun", "The process of converting information or data into a code, especially to prevent unauthorized access.", "coding", "scrambling", "ciphering")));
        words.add(createWord("ARTIFICIAL", 
            createSimpleMeaning("adjective", "Made or produced by human beings rather than occurring naturally.", "synthetic", "man-made", "fabricated")));
        allLevels.add(new Level(45, words, "Level 45: Computer Tech"));

        // === Level 46: Philosophy 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("PHILOSOPHY", 
            createSimpleMeaning("noun", "The study of the fundamental nature of knowledge, reality, and existence.", "thinking", "reasoning", "wisdom")));
        words.add(createWord("PERCEPTION", 
            createSimpleMeaning("noun", "The ability to see, hear, or become aware of something through the senses.", "awareness", "understanding", "recognition")));
        words.add(createWord("CONSCIENCE", 
            createSimpleMeaning("noun", "An inner feeling or voice viewed as acting as a guide to the rightness or wrongness of one's behavior.", "morality", "principles", "ethics")));
        words.add(createWord("EXISTENTIAL", 
            createSimpleMeaning("adjective", "Concerned with existence, especially human existence as viewed in the theories of existentialism.", "philosophical", "experiential", "ontological")));
        allLevels.add(new Level(46, words, "Level 46: Deep Thinking"));

        // === Level 47: Literature 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("LITERATURE", 
            createSimpleMeaning("noun", "Written works, especially those considered of superior or lasting artistic merit.", "writings", "books", "publications")));
        words.add(createWord("VOCABULARY", 
            createSimpleMeaning("noun", "The body of words used in a particular language or in a particular sphere.", "words", "terminology", "lexicon")));
        words.add(createWord("EXPRESSION", 
            createSimpleMeaning("noun", "The process of making known one's thoughts or feelings.", "utterance", "articulation", "communication")));
        words.add(createWord("ALLITERATE", 
            createSimpleMeaning("verb", "Use words that begin with the same sound near one another.", "repeat", "echo", "resonate")));
        allLevels.add(new Level(47, words, "Level 47: Advanced Writing"));

        // === Level 48: Geography 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("GEOLOGICAL", 
            createSimpleMeaning("adjective", "Relating to the study of the physical structure and substance of the earth.", "terrestrial", "earthen", "mineral")));
        words.add(createWord("TOPOGRAPHY", 
            createSimpleMeaning("noun", "The arrangement of the natural and artificial physical features of an area.", "terrain", "landscape", "geography")));
        words.add(createWord("ATMOSPHERE", 
            createSimpleMeaning("noun", "The envelope of gases surrounding the earth or another planet.", "air", "environment", "ambiance")));
        words.add(createWord("CARTOGRAPH", 
            createSimpleMeaning("verb", "Create or draw maps.", "map", "chart", "plot")));
        allLevels.add(new Level(48, words, "Level 48: Earth Science"));

        // === Level 49: Economics 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("INVESTMENT", 
            createSimpleMeaning("noun", "The action or process of investing money for profit or material result.", "funding", "financing", "backing")));
        words.add(createWord("PRODUCTION", 
            createSimpleMeaning("noun", "The action of making or manufacturing from components or raw materials.", "creation", "manufacture", "assembly")));
        words.add(createWord("MANAGEMENT", 
            createSimpleMeaning("noun", "The process of dealing with or controlling things or people.", "administration", "direction", "control")));
        words.add(createWord("ENTERPRISE", 
            createSimpleMeaning("noun", "A project or undertaking, typically one that is difficult or requires effort.", "venture", "business", "undertaking")));
        allLevels.add(new Level(49, words, "Level 49: Business World"));

        // === Level 50: Advanced Concepts 10-letter Words ===
        words = new ArrayList<>();
        words.add(createWord("INNOVATION", 
            createSimpleMeaning("noun", "The action or process of innovating; a new method, idea, product, etc.", "creation", "invention", "originality")));
        words.add(createWord("REVOLUTION", 
            createSimpleMeaning("noun", "A dramatic and wide-reaching change in conditions, attitudes, or operation.", "change", "upheaval", "transformation")));
        words.add(createWord("COMPLEXITY", 
            createSimpleMeaning("noun", "The state or quality of being intricate or complicated.", "intricacy", "difficulty", "complication")));
        words.add(createWord("PHENOMENON", 
            createSimpleMeaning("noun", "A fact or situation that is observed to exist or happen, especially one whose cause or explanation is in question.", "occurrence", "event", "happening")));
        allLevels.add(new Level(50, words, "Level 50: Master Level"));
    }

    /**
     * Gets the current Level object based on currentLevelIndex.
     * This method DOES NOT advance the level itself.
     * @return The current Level object, or null if currentLevelIndex is out of bounds or all levels done.
     */
    public Level getCurrentLevel() {
        // System.out.println("GM_DEBUG: getCurrentLevel() called. currentLevelIndex: " + currentLevelIndex);
        if (currentLevelIndex >= 0 && currentLevelIndex < allLevels.size()) {
            return allLevels.get(currentLevelIndex);
        }
        // System.out.println("GM_DEBUG: getCurrentLevel() returning null.");
        return null; // Signifies no current level (either before start or after all levels completed)
    }

    /**
     * Advances to the next word in the game, moving to the next level if necessary.
     * This is the primary method for progressing through words and levels.
     * Sets currentWordToGuess internally.
     * @return The next Word object to guess, or null if all levels are completed.
     */
    public Word getNextWord() {
        // System.out.println("GM_DEBUG: getNextWord() ENTRY. currentLevelIndex: " + currentLevelIndex + ", currentWordIndexInLevel: " + currentWordIndexInLevel);
        hintsUsedForCurrentWord = 0;

        currentWordIndexInLevel++; // Try to advance word in current level

        if (currentLevelIndex < allLevels.size()) {
            Level actualCurrentLevelObject = allLevels.get(currentLevelIndex);
            // System.out.println("GM_DEBUG: getNextWord() - Current Level " + actualCurrentLevelObject.getLevelNumber() + " has " + actualCurrentLevelObject.getWordsToGuess().size() + " words.");

            if (currentWordIndexInLevel < actualCurrentLevelObject.getWordsToGuess().size()) {
                currentWordToGuess = actualCurrentLevelObject.getWordsToGuess().get(currentWordIndexInLevel);
                // System.out.println("GM_DEBUG: getNextWord() - SUCCESS in same level. Word: " + currentWordToGuess.getWord() + " (L:" + (currentLevelIndex+1) + ", W:" + (currentWordIndexInLevel+1) + ")");
                return currentWordToGuess;
            } else {
                // End of current level, try to advance to next level
                // System.out.println("GM_DEBUG: getNextWord() - End of Level " + (currentLevelIndex + 1) + ". Attempting to advance level index.");
                currentLevelIndex++;
                if (currentLevelIndex < allLevels.size()) {
                    currentWordIndexInLevel = 0; // Reset for new level
                    currentWordToGuess = allLevels.get(currentLevelIndex).getWordsToGuess().get(currentWordIndexInLevel);
                    // System.out.println("GM_DEBUG: getNextWord() - SUCCESS moved to new Level " + (currentLevelIndex + 1) + ". Word: " + currentWordToGuess.getWord() + " (L:" + (currentLevelIndex+1) + ", W:" + (currentWordIndexInLevel+1) + ")");
                    return currentWordToGuess;
                } else {
                    currentWordToGuess = null; // All levels completed
                    // System.out.println("GM_DEBUG: getNextWord() - NO MORE LEVELS. currentLevelIndex is now " + currentLevelIndex);
                    return null;
                }
            }
        } else {
            currentWordToGuess = null; // Already past the last level
            // System.out.println("GM_DEBUG: getNextWord() - NO MORE LEVELS (initial check). currentLevelIndex is " + currentLevelIndex);
            return null;
        }
    }

    public Word getCurrentWordToGuess() {
        // This should ideally return the word that getNextWord() last set.
        return currentWordToGuess;
    }

    public String getJumbledWord() { // Not used in Wordle grid, but keeping
        if (currentWordToGuess == null) return "";
        String word = currentWordToGuess.getWord();
        List<Character> characters = new ArrayList<>();
        for (char c : word.toUpperCase().toCharArray()) { characters.add(c); }
        Collections.shuffle(characters, new Random(System.nanoTime()));
        StringBuilder jumbled = new StringBuilder();
        for (char c : characters) { jumbled.append(c); }
        if (jumbled.toString().equals(word.toUpperCase()) && word.length() > 1) {
            return getJumbledWord();
        }
        return jumbled.toString();
    }

    public boolean checkGuess(String guess) { // Not used directly by GameActivity if GameActivity does the check
        if (currentWordToGuess == null || guess == null) return false;
        return currentWordToGuess.getWord().equalsIgnoreCase(guess.trim());
    }

// In GameManager.java (MODIFIED METHOD SIGNATURE AND LOGIC)
    /**
     * Provides a hint for the given Word object.
     * GameActivity should pass the current word it is displaying.
     * @param currentWordObject The Word object currently being guessed.
     * @return A hint string.
     */
    public String getHint(Word currentWordObject) { // Parameter changed
        if (currentWordObject == null) return "No word loaded for hints.";

        String actualWord = currentWordObject.getWord();
        hintsUsedForCurrentWord++; // Increment hints used for *this specific word*

        // Reveal letters one by one
        if (hintsUsedForCurrentWord <= actualWord.length()) {
            StringBuilder hintText = new StringBuilder("Hint: The word is ");
            for (int i = 0; i < actualWord.length(); i++) {
                if (i < hintsUsedForCurrentWord) {
                    hintText.append(actualWord.charAt(i));
                } else {
                    hintText.append("_"); // Use underscore for unrevealed letters
                }
                if (i < actualWord.length() -1) hintText.append(" ");
            }
            return hintText.toString();
        } else if (currentWordObject.getMeanings() != null && !currentWordObject.getMeanings().isEmpty()) {
            Word.Meaning firstMeaning = currentWordObject.getMeanings().get(0);
            // Example: give part of speech after all letters revealed
            if (hintsUsedForCurrentWord == actualWord.length() + 1) {
                return "Hint: Part of speech is " + firstMeaning.getPartOfSpeech() + ".";
            }
            // Example: give a synonym
            if (hintsUsedForCurrentWord == actualWord.length() + 2 &&
                    firstMeaning.getSynonyms() != null &&
                    !firstMeaning.getSynonyms().isEmpty()) {
                return "Hint: A synonym is '" + firstMeaning.getSynonyms().get(0) + "'.";
            }
            // You can add more hint types here, e.g., first letter of definition
        }
        return "No more hints for this word.";
    }

    /**
     * Returns the 1-based number of the current word being attempted within its level.
     */
    public int getCurrentWordNumberInLevelForDisplay() {
        return currentWordIndexInLevel + 1;
    }

    /**
     * Checks if the GameManager's internal currentWordIndexInLevel is at the last word
     * of the GameManager's internal currentLevelData.
     */
    public boolean isLastWordInCurrentManagerLevel() {
        if (currentLevelIndex < allLevels.size()) {
            Level level = allLevels.get(currentLevelIndex);
            return currentWordIndexInLevel >= level.getWordsToGuess().size() - 1;
        }
        return true; // No current level or past all levels
    }

    /**
     * Advances the game to the next level.
     * @return true if successfully advanced to a new valid level, false if all levels are completed.
     */
    public boolean advanceToNextLevel() {
        if (currentLevelIndex < allLevels.size() - 1) {
            currentLevelIndex++;
            System.out.println("GM_DEBUG: Advanced to level index: " + currentLevelIndex);
            return true; // Advanced to a new level
        }
        // Already at or past the last level
        currentLevelIndex = allLevels.size(); // Ensure it's marked as definitely past all levels
        System.out.println("GM_DEBUG: No more levels. currentLevelIndex is now: " + currentLevelIndex);
        return false; // No more levels to advance to
    }

    /**
     * Gets the number of hints still available for the current word.
     * @return Number of hints remaining
     */
    public int getAvailableHints() {
        return MAX_HINTS_PER_WORD - hintsUsedForCurrentWord;
    }

    /**
     * Uses one hint for the current word.
     * @return true if hint was successfully used, false if no hints remaining
     */
    public boolean useHint() {
        if (hintsUsedForCurrentWord < MAX_HINTS_PER_WORD) {
            hintsUsedForCurrentWord++;
            return true;
        }
        return false;
    }

    /**
     * Resets the hint counter for a new word.
     * Should be called by GameActivity when it prepares a new word for guessing.
     */
    public void resetHintsForNewWord() {
        hintsUsedForCurrentWord = 0;
    }

    /**
     * Checks if the GameManager's internal currentLevelIndex has moved past all defined levels.
     */
    public boolean areAllGameManagerLevelsCompleted() {
        return currentLevelIndex >= allLevels.size();
    }

    /**
     * Returns the current level index (0-based).
     * @return The current level index, or -1 if no levels are available.
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < allLevels.size()) {
            this.currentLevelIndex = levelIndex;
        }
    }
}