;=====================
myStack SEGMENT STACK
    DB 256 DUP (?)
myStack ENDS
;=====================

;=====================
myData SEGMENT
    randomLetters DB 3 DUP (?)
    copiedLetters DB 3 DUP (?)
    seed DW 1
    targetWaitTime DW (?)
    numCorrect DB 0

    
myData ENDS
;=====================

myCode SEGMENT
    assume ds: myData, cs: myCode

;=====================
main PROC
    mov ax, 0b800h
    mov es, ax
    mov ax, myData
    mov ds, ax

    ; clear screen
    call clearScreen
    call pickRandomChars
    call copyChars
    call waitTime
    call scatterChars

topMainLoop:
    call showCorrect
    call showLetterSets
    mov ah, 11h
    int 16h
    jz topMainLoop
    mov ah, 10h
    int 16h ; get key from buffer
    cmp al, 27
    je exitMain
    call handleChar
    jmp topMainLoop

exitMain:
    call endGame

main ENDP
;=====================

;=====================
showCorrect PROC
    push ax
    mov al, numCorrect
    add al, '0'
    mov es:[160], al
    pop ax
    ret
showCorrect ENDP
;=====================

;=====================
showLetterSets PROC
    push ax cx si di

    lea si, randomLetters
    mov di, 320
    mov cx, 0
topShowOriginal:
    mov al, ds:[si]
    mov es:[di], al         ; mov char from randomLetters onto screen space di
    inc cx                  ; next char offset
    inc si
    add di, 2               ; next screen pos
    cmp cx, 3               ; are we at last char
    jne topShowOriginal     ; if not, then go to next char

    lea si, copiedLetters   ; if so, go to copied letters
    mov di, 480
    mov cx, 0
topShowCopied:
    mov al, ds:[si]
    mov es:[di], al         ; mov char from randomLetters onto screen space di
    inc cx                  ; next char offset
    inc si
    add di, 2               ; next screen pos
    cmp cx, 3               ; are we at last char
    jne topShowCopied     ; if not, then go to next char

    pop di si cx ax
    ret
showLetterSets ENDP
;=====================

;=====================
endGame PROC
    mov ah, 4ch
    int 21h
endGame ENDP
;=====================

;=====================
copyChars PROC
    push ax cx si di
    lea si, randomLetters
    lea di, copiedLetters
    mov cx, 0

topCopyLoop:
    cmp cx, 3
    je exitCopy
    mov al, ds:[si] ; mov char into al
    mov ds:[di], al ; mov char in al into ds:[di] (the copiedLetters)
    inc si          ; go to next char in randomLetters
    inc di          ; go to next char in copiedLetters
    inc cx          ; inc cx
    jmp topCopyLoop

exitCopy:
    pop di si cx ax
    ret
copyChars ENDP
;=====================

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
;on entry:
;       look at the CARRY flag, if its true, then that means the player typed a correct char

handleGameState PROC
    cmp ax, 1
    je foundCorrectChar ; if the carry flag is true, that means the player typed a good character
    mov numCorrect, 0   ; if they didn't, then reset the num correct
    call copyChars      ; reset all typed chars
    ret

foundCorrectChar:
    inc numCorrect
    cmp numCorrect, 3
    je playerWin
    ret

playerWin:
    call endGame
    ret

handleGameState ENDP
;=====================

;=====================
handleChar PROC
; on entry:
;   AL: contains the character we are looking for
; on exit:
;   Carry flag will be true if its found
    push cx di

    lea di, copiedLetters   ; di will contain address for copiedLetters
    mov cx, 0

topHandleChar:
    cmp cx, 3
    je charNotFound ; if we reached the end and we haven't found anything, return with carry flag = 0
    cmp al, ds:[di] ; is the letter at di the same as what we typed?
    je charFound    ; if so, then go to charFound
    inc di
    inc cx
    jmp topHandleChar

charFound:
    mov ds:[di], byte ptr 0 ; set the char at this position to 0
    pop di cx
    mov ax, 1                     ; set carry flag, meaning we got a char correct
    call handleGameState
    ret

charNotFound:
    pop di cx
    mov ax, 0
    call handleGameState
    ret
handleChar ENDP
;=====================

;=====================
pickRandomChars PROC
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
    push word ptr 26    ; high range of getRandomNumber
    call getRandomNumber; puts random number 0-25 in AX
    add sp, 2           ; "clears" stack
    mov ah, 0           ; clear ah
    add al, 'a'         ; adds 'a' so we can get the actual character
    call letterInList   ; AX will contain 1 if the letter is already in the 'randomLetters'
    jc topPickChar      ; if the carry flag is true, then that means we found that character, jump back to top
    mov ds:[si], al
    inc si

    inc bx
    jmp topPickChar

exitPickRandomChar:
    pop di si bx ax
    ret
pickRandomChars ENDP
;=====================

;=====================
letterInList PROC
; on entry:
;   AL: contains the character we are looking for
; on exit:
;   Carry flag will be true if its found
    cmp al, randomLetters
    je foundDuplicate
    cmp al, randomLetters+1
    je foundDuplicate
    cmp al, randomLetters+2
    jne notFound

foundDuplicate:
    stc
    ret

notFound:
    clc
    ret
letterInList ENDP
;=====================

;=====================
getRandomNumber PROC
    push bp
    mov bp, sp
    push bx

    mov ah, 00h ; stores low order of ticks in DX
    int 1ah

    mov ax, dx
    mov bx, seed    ; move the previous seed into bx
    mul bx          
    add ax, 1123    ; add large prime number

    ; the process above is equal to: a*r0+b where a and b are large prime numbers
    
    mov dx, 0       ; clear dx of any thing to allow for a divide

    mov bx, [bp+4]  ; move the high bound into bl
    div bx          ; divide ax by the high bound

    mov ax, dx  ; put remainder into ax
    mov seed, ax    ; make the seed the last result

    pop bx bp
    ret
getRandomNumber ENDP
;=====================

;=====================
scatterChars PROC
    push ax di
    ; MAKE THIS INTO A LOOP
    push word ptr 1920
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, copiedLetters
    mov es:[di], al
    
    push word ptr 1920
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, copiedLetters+1
    mov es:[di], al
    
    push word ptr 1920
    call getRandomNumber
    add sp, 2
    shl ax, byte ptr 1
    mov di, ax
    mov al, copiedLetters+2
    mov es:[di], al

    pop di ax
    ret
scatterChars ENDP
;=====================

;=====================
waitTime PROC
    push ax bx cx dx

    mov ah, 00h  
    int 1ah         ; gets ticks CX:DX
                    ; dx has low order

    mov targetWaitTime, dx  ; make that our BASE target time

    push word ptr 30
    call getRandomNumber    ; ax will contain 0 - 30
    add sp, 2
    add ax, 30              ; ax will contain 30 - 60 tenths
    add targetWaitTime, ax

topWaitLoop:
    mov ah, 00h
    int 1ah         ; gets ticks CX:DX

    cmp dx, targetWaitTime 
    jge exitWaitLoop
    jmp topWaitLoop

exitWaitLoop:
    pop dx cx bx ax
    ret
waitTime ENDP
;=====================


myCode ENDS

end main
