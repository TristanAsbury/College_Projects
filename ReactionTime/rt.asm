;=================
myStack SEGMENT STACK
    DB 256 DUP (?)
myStack ENDS
;=================

;=================
myData SEGMENT
    randomLetters DB 3 DUP (?)
    typedLetters DB 3 DUP (?)
myData ENDS
;=================

myCode SEGMENT
    assume ds: myData, cs: myCode

main PROC
    mov ax, 0b800h
    mov es, ax
    mov ax, myData
    mov ds, ax

    ; clear screen
    call clearScreen
    call pickRandomChar

    mov ah, 4ch
    int 21h

main ENDP

;=====================
clearScreen PROC
    push si
    mov si, 0

topClearLoop:
    cmp si, 4000
    je exitClear
    mov es:[si], byte ptr ' '
    mov es:[si+1], byte ptr 00001111b
    add si, 2
    jmp topClearLoop

exitClear:
    pop si
    ret
clearScreen ENDP
;=====================

;=====================
pickRandomChar PROC
    ; use the random number generator
    ; compare that character to the characters currently in randomLetters (using a loop)
    ; if that character is already there, then find another random number, keep doing this

    ; once we have a new character, then display it somewhere random on the screen
    ; do this by using the random number generator, 
    push ax bx si di
    mov bx, 0   ; this keeps count of how many successful characters have been pushed into 'randomLetters'
    lea si, randomLetters   ; pointing to the first character at 'randomLetters'
    mov di, 160

topPickChar:
    cmp bx, 3
    je exitPickRandomChar

    push byte ptr 26    ; high range of getRandomNumber
    call getRandomNumber; puts random number 0-25 in ah
    add sp, 2           ; "clears" stack
    add ah, 'a'         ; adds 'a' so we can get the actual character
    
    push ax             ; preserve ax (containing the character)
    call letterInList   ; ax will contain 1 if the letter is already in the 'randomLetters'
    cmp ax, 1           ; is ax 1?
    pop ax              ; get character back
    je topPickChar      ; if so, then goBack to top wihtout adding the character

    mov es:[di], byte ptr ah    ; else, put the character on the screen
    mov es:[di+1], byte ptr 00001111b
    add di, 2
    inc bx
    jmp topPickChar

exitPickRandomChar:
    pop di si bx ax
    ret
pickRandomChar ENDP
;=====================

;=====================
letterInList PROC
; on entry:
;   ah: contains the character we are looking for
    push si cx
    lea si, randomLetters   ; si is pointing to the randomLetter list
    mov cx, 0               ; cx is our counter (also the offset for si)
    mov ax, 0

topLetterInList:
    cmp cx, 3               ; are we at the end of the character list?
    je exitLetterInList     ; if so, exit this proc
    add si, cx              ; else, add the offset to si
    cmp ds:[si], ah         ; compare, is the character at ds:[si] the same as the one we are looking for
    inc cx                  ; inc cx
    jne topLetterInList     ; if they arent equal, go to next character
    mov ax, 1               ; else, move into ax, 1

exitLetterInList:
    pop cx si 
    ret
letterInList ENDP
;=====================

;=========================================
getRandomNumber PROC
    push bp
    mov bp, sp
    push bx

    mov ah, 00h ; stores low order of ticks in DX
    int 1ah

    mov ax, dx  ; AH contains remainder, put that into dx
    mov ah, 0   ; put 0 into ah

    mov bl, 26  ; move 10 into bl
    div bl      ; divide ax by 10

    mov al, 0

    pop bx bp
    ret
getRandomNumber ENDP
;=========================================

myCode ENDS

end main
