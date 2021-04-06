# Kata 3 - How Big? How Fast?

## How Big?
*Roughly how many binary digits (bit) are required for the unsigned representation of:*

---
Conversion steps: 
1. Divide the number by 2.
2. Get the integer quotient for the next iteration.
3. Get the remainder for the binary digit.
4. Repeat the steps until the quotient is equal to 0.

Hard to do in head.. Used pen/paper ..and then clojure

- 1,000             => 1111101000                                  => **10**
- 1,000,000         => 11110100001001000000                        => **20**
- 1,000,000,000     => 111011100110101100101000000000              => **30**
- 1,000,000,000,000 => 1110100011010100101001010001000000000000    => **40**
- 8,000,000,000,000 => 1110100011010100101001010001000000000000000 => **43**

---
*My town has approximately 20,000 residences. How much space is required to store the names, addresses, and a phone number for all of these (if we store them as characters)?*

---
Assumptions:
- Char is 8 bytes
- Average first name length: 7 letters (informal analysis based on "top names" lists)
- Average last name length: 7 letters (sure, why not?)
- Average address: [4 digit address + 1 letter directional + 4 letter street name + 2 letter street-type] => 1234 E mill st => 14 chars (including spaces)
- 10 digit phone number
---------------------------
Total chars required to store: 38

38 * 20,000 = 760,000 bytes
760,000 / 8 = 95,000 bytes = **95 kb**

---
*I’m storing 1,000,000 integers in a binary tree. Roughly how many nodes and levels can I expect the tree to have? Roughly how much space will it occupy on a 32-bit architecture?*

---
Let's say it's 10 integers
1st pass => 2  nodes 1-5 | 6 -10
2nd pass => 4  nodes 1-3 | 4-5 | 6-8 | 9-10
3rd pass => 8  nodes 1-2 | 3 | 4 | 5 | 6-7 | 8 | 9 | 10
4th pass => 10 nodes 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10

So nodes = **1,000,000**
levels = **1,000,000 / 2 - 2** ... no idea if this is accurate TODO

## How Fast?

*My copy of Meyer’s Object Oriented Software Construction has about 1,200 body pages. Assuming no flow control or protocol overhead, about how long would it take to send it over an async 56k baud modem line?*

---
Assumptions:
- Could be raw text or a PDF with the formatting.. 
- I'm assuming raw text for this
- 1 page raw text at 12px font with standard margins has ~ 2 kilobytes of data (according to the internet).
- 2 kb * 1200 pages = 24,000 kb
- 56,000 / 24,000 = **2.3s**
---
*My binary search algorithm takes about 4.5mS to search a 10,000 entry array, and about 6mS to search 100,000 elements. How long would I expect it to take to search 10,000,000 elements (assuming I have sufficient memory to prevent paging).*

___
- I don't get the speeding up in these numbers... I would expect 100,000 to take 45ms (10x)
- 10,000 / 4.5ms = 2222 items / ms
- 100,000 / 6ms = 16667 items / ms
- So I would probably take an average of the items / ms
- but using these numbers it looks like as the size increases by 10x the speed only by +33%
- 1,000,000 => 8ms
- 10,000,000 = **10.67ms**

---
*Unix passwords are stored using a one-way hash function: the original string is converted to the ‘encrypted’ password string, which cannot be converted back to the original string. One way to attack the password file is to generate all possible cleartext passwords, applying the password hash to each in turn and checking to see if the result matches the password you’re trying to crack. If the hashes match, then the string you used to generate the hash is the original password (or at least, it’s as good as the original password as far as logging in is concerned). In our particular system, passwords can be up to 16 characters long, and there are 96 possible characters at each position. If it takes 1mS to generate the password hash, is this a viable approach to attacking a password?*

---
There are number of chars possible to the number of chars length power 
(POSSIBLE EXP LENGTH)

For example:
---
2 chars possible and 2 length = 4
| 1 | 2 |
|---|---|
| a | a |
| b | b |
| a | b |
| b | a |

3 chars possible and 2 length = 8
|1 |2
|--|--
|a |a
|b |b
|c |c
|a |b
|b |a
|a |c
|c |a
|b |c
|c |b

2 chars possible and 3 length = 9
|1 |2 |3
|--|--|--
|a |a |a
|a |a |b
|a |b |a
|b |a |a
|b |b |b
|b |b |a
|b |a |b
|b |a |a

So using that formula, I'd have 96 to the 16th power = 
(Math/pow 96 16) => 
52040292466647270000000000000000 msecs =>
52040292466647270000000000000 secs =>
867338207777454500000000000 minutes =>
14455637000000000000000000 hours =>
602318220000000000000000 days =>
1649057412731006000000 years =>
16490574127310059520 centuries =>
**NO, not viable approach**


