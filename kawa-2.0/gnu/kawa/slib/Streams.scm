;; Based upon the SRFI-41 reference implementation which is:
;; Copyright (C) Philip L. Bewig (2007). All Rights Reserved.

;; Permission is hereby granted, free of charge, to any person
;; obtaining a copy of this software and associated documentation
;; files (the "Software"), to deal in the Software without
;; restriction, including without limitation the rights to use, copy,
;; modify, merge, publish, distribute, sublicense, and/or sell copies
;; of the Software, and to permit persons to whom the Software is
;; furnished to do so, subject to the following conditions:

;; The above copyright notice and this permission notice shall be
;; included in all copies or substantial portions of the Software.

;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
;; BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
;; ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
;; CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;; SOFTWARE.

;; Translation to Kawa: Copyright (C) Jamison Hope (2010).

;; This Kawa module exports SRFI-41's (streams) library.

(provide 'srfi-41)
(require 'srfi-41-streams-primitive)
(require 'srfi-41-streams-derived)

(module-compile-options warn-undefined-variable: #t
                        warn-unknown-member: #t)

(module-export stream-null stream-cons stream? stream-null?
               stream-pair? stream-car stream-cdr stream-lambda
               define-stream list->stream port->stream stream
               stream->list stream-append stream-concat
               stream-constant stream-drop stream-drop-while
               stream-filter stream-fold stream-for-each stream-from
               stream-iterate stream-length stream-let stream-map
               stream-match stream-of stream-range stream-ref
               stream-reverse stream-scan stream-take
               stream-take-while stream-unfold stream-unfolds
               stream-zip)
