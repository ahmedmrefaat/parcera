(ns parsero.core
  (:require [instaparse.core :as instaparse]
            [clojure.data :as data]))


(def grammar
    "file: forms

    whitespace = #'[,\\s]*'

    <forms>: form* ;

    <form>: <whitespace> ( literal
                          | symbol
                          | list
                          | vector
                          | map
                          | set
                          | reader_macro
                          )
            <whitespace>;

    list: <'('> forms <')'> ;

    vector: <'['> forms <']'> ;

    map: <'{'> (form form)* <'}'> ;

    set: <'#{'> forms <'}'> ;

    <literal>:
          number
        | string
        | character
        | keyword
        | <COMMENT>
        ;

    keyword: SIMPLE_KEYWORD | MACRO_KEYWORD;

    number: DOUBLE | RATIO | LONG;

    character: <'\\\\'> ( SIMPLE_CHAR | UNICODE_CHAR );

    <reader_macro>:
          dispatch
        | metadata
        | deref
        | quote
        | backtick
        | unquote
        | unquote_splicing
        ;

    dispatch: <'#'> ( function | regex | var_quote | discard | tag)

    function: list;

    metadata: <'^'> ( map_metadata | shorthand_metadata );

    <map_metadata>: map form

    <shorthand_metadata>: ( symbol | string | keyword ) form;

    regex: string;

    var_quote: <'\\''> symbol;

    quote: <'\\''> form;

    backtick: <'`'> form;

    unquote: <'~'> form;

    unquote_splicing: <'~@'> form;

    deref: <'@'> form;

    discard: <'_'> form;

    tag: !'_' symbol form;

    string : <'\"'> #'[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*' <'\"'>;

    symbol: !SYMBOL_HEAD (VALID_CHARACTERS <'/'>)? (VALID_CHARACTERS | '/') !'/';

    (* Lexers -------------------------------------------------------------- *)

    SIMPLE_KEYWORD: <':'> !':' (VALID_CHARACTERS <'/'>)? VALID_CHARACTERS !'/';

    MACRO_KEYWORD: <'::'> VALID_CHARACTERS;

    <DOUBLE>: #'[-+]?(\\d+(\\.\\d*)?([eE][-+]?\\d+)?)(M)?'

    <RATIO>: #'[-+]?(\\d+)/(\\d+)'

    <LONG>: #'[-+]?(?:(0)|([1-9]\\d*)|0[xX]([\\dA-Fa-f]+)|0([0-7]+)|([1-9]\\d?)[rR]([\\d\\w]+)|0\\d+)(N)?'
            !'.';

    COMMENT: <';'> #'.*';

    <UNICODE_CHAR>: <'u'> #'[\\dD-Fd-f]{4}';

    <SIMPLE_CHAR>:
          'newline'
        | 'return'
        | 'space'
        | 'tab'
        | 'formfeed'
        | 'backspace'
        | #'.';

    (* fragments *)
    (*
    ;; symbols cannot start with number, :, #
    ;; / is a valid symbol as long as it is not part of the name
    ;; note: added ' as invalid first character due to ambiguity in #'hello
    ;; -> [:tag [:symbol hello]]
    ;; -> [:var_quote [:symbol hello]]
    *)
    SYMBOL_HEAD: number | ':' | '#' | '\\''

    (*
    ;; NOTE: several characters are not allowed according to clojure reference.
    ;; https://clojure.org/reference/reader#_symbols
    ;; EDN reader says otherwise https://github.com/edn-format/edn#symbols
    ;; nil, true, false are actually symbols with special meaning ... not grammar rules
    ;; on their own
    *)
    <VALID_CHARACTERS>: #'[\\w.*+\\-!?$%&=<>\\':#]+'")

(def clojure (instaparse/parser grammar))

#_(data/diff (first (instaparse/parses clojure (slurp "./src/parsero/core.clj")))
             (second (instaparse/parses clojure (slurp "./src/parsero/core.clj"))))

;(count (instaparse/parses clojure (slurp "./src/parsero/core.clj")))

;(time (clojure (slurp "./src/parsero/core.clj") :unhide :all))

;(dotimes [n 100])
(time (clojure (slurp "./src/parsero/core.clj")))
