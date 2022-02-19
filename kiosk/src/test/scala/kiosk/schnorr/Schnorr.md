# Schnorr Signatures

## Initial Setup

Ergo uses the same curve as Bitcoin (Secp256k1), which we call *G*. The curve also defines a default generator *g*.

1. Secret key is integer *x* 
2. Public key is *Y = g^x*, an element of *G*

## Signing

Let the hash of the message to be signed be *M*. The signature is computed as follows:

1. Generate a random integer *r* and compute *U = g^r*. 
2. Compute the integer *c = Hash(U || M)* 
3. Compute *s = r - cx*.
4. Send the value *(c, s)* to the verifier as the "signature"

Note that the signature is a *pair of integers*.

## Verification

### Schnorr Identification

To understand verification, first consider a variant called "Schnorr identification".
In this, instead of *(c, s)*, the value *(U, s)* -- a group element and an integer -- is sent.

The verifier computes *c = Hash(U || M)* and accepts iff *g^s = U / Y^c*.

This works because LHS *= g^s = g^(r - cx) = g^r / (g^x)^c =* RHS.  

The signature uses the fact that knowledge of *c* is equivalent to knowledge of *U*.

### Schnorr Signature Verification

Given the signature *(c, s)*, we perform the "reverse" of the identification in some sense.

Recall that the verifier of the identification scheme computes *c* from *U* using *Hash* and then verifies some condition.

The verifier of the signature scheme instead computes *U* from *c* using the condition and verifies *Hash*.

In other words, the verifier first computes *U = g^s * Y^c* and accepts if *c = Hash(U || M)*.

## Verification in ErgoScript

There are several use-cases where we need to verify a Schnorr signature in ErgoScript.

We use the following setup in our example: 

1. The public key *Y* is provided as a `GroupElement` in R4. 
2. The message *m* is provided as a `Coll[Byte]` in context variable 0.
3. The value *c* of the signature is provided as a `Coll[Byte]` (for convenience) in context variable 1.
4. The value *s* of the signature is provided as a `BigInt` in context variable 2.
5. The hash function is Sha256. 

The following is the script.

```scala
{
  // Checking Schnorr signature in a script
  val g: GroupElement = groupGenerator

  // Public key for a signature
  val Y = SELF.R4[GroupElement].get

  // Message to sign
  val m = getVar[Coll[Byte]](0).get

  // c of signature in (c, s)
  val cBytes = getVar[Coll[Byte]](1).get
  val c = byteArrayToBigInt(cBytes)
  
  // s of signature in (c, s)
  val s = getVar[BigInt](2).get
  
  // Computing challenge
  
  val U = g.exp(s).multiply(Y.exp(c)).getEncoded // as a byte array
  
  sigmaProp(cBytes == sha256(U ++ m))
}
```