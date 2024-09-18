package org.zcb.common.utils

import org.apache.commons.codec.binary.Base64
import sun.security.util.{DerInputStream, DerValue}

import java.math.BigInteger
import java.security.spec.RSAPrivateCrtKeySpec
import java.security.{GeneralSecurityException, KeyFactory, PrivateKey}
import javax.crypto.Cipher


object RSAUtil {
    val privKey: String =
        """-----BEGIN RSA PRIVATE KEY-----
          |-----END RSA PRIVATE KEY-----""".stripMargin
    val pubKey: String =
        """-----BEGIN RSA PUBLIC KEY-----
          |-----END RSA PUBLIC KEY-----""".stripMargin


    val privKeyByteArray: Array[Byte] = Base64.decodeBase64(
        privKey.stripPrefix("-----BEGIN RSA PRIVATE KEY-----")
            .stripSuffix("-----END RSA PRIVATE KEY-----")
            .split("\r\n")
            .map(_.trim.filter(_ >= ' '))
            .mkString)
    val priv: PrivateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS1EncodedRSAPrivateKeySpec(privKeyByteArray))

    def decrypt(content: Array[Byte]): String = {
        val cipher: Cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, priv)
        new String(cipher.doFinal(content))
    }

    def main(args: Array[String]): Unit = {
        val content: String = """8f64646fd049ddfffd314d7539fa5fc4a9d31d00e9bc87e5538c944be8187d9f892b9ab289d79d9e502bb660af531d04fe8e9f2d6f7daa0737cb8d5332b32409fb45ffaa788532de2d3c2107c78a53f9859874f93844d23e80c71851935d5302a696422ced8fef33c8a7f84d941410e9d37c76876d78e5f71d3a6f1aa0e97eb063f85347c3e9abe0a4ea83f0b431fe7c2878aa35cae6cbd1f0add57be5472d536da4e3b32ef24a8c2f6835f81f063e34dba7dea9c35f0a54f96a73d810390f74bc1e5126ed8b44b2f90281db01d5aaf897838a96d31e41a975b7e37dd25818a95645f06429b4f7334ff441dd44a324f27b8c2dfae737de8fb2fb23c33993d1a4"""
        val contentArrayByte = BigInt(content, 16).toByteArray
        val content_decrypt = decrypt(contentArrayByte.takeRight(contentArrayByte.length - 1))
        val content_json = new String(Base64.decodeBase64(content_decrypt), "utf-8")
        import org.json4s._
        import org.json4s.jackson.JsonMethods._
        val t = parse(content_json)
        val tt = t.values.asInstanceOf[scala.collection.immutable.Map[String, Any]]
        val tttt = tt.getOrElse("host", "")
        val ttt = ""
    }

}

class PKCS1EncodedRSAPrivateKeySpec(encodedKey: Array[Byte]) extends RSAPrivateCrtKeySpec(
    RsaByteArraySequence.getModulus(encodedKey),
    RsaByteArraySequence.getPublicExponent(encodedKey),
    RsaByteArraySequence.getPrivateExponent(encodedKey),
    RsaByteArraySequence.getPrimeP(encodedKey),
    RsaByteArraySequence.getPrimeQ(encodedKey),
    RsaByteArraySequence.getPrimeExponentP(encodedKey),
    RsaByteArraySequence.getPrimeExponentQ(encodedKey),
    RsaByteArraySequence.getCrtCoefficient(encodedKey)) {

    def getEncoded: Array[Byte] = this.encodedKey

    def getFormat: String = "PKCS#1"
}

private object RsaByteArraySequence {
    private def getSequence(encodedKey: Array[Byte]): Array[DerValue] = {
        val derInputStream = new DerInputStream(encodedKey)
        val sequence = derInputStream.getSequence(0)

        // If sequence length is less than 9, something is wrong with the input byte array
        if (sequence.length < 9) {
            throw new GeneralSecurityException("Could not parse a PKCS1 private key.")
        }

        sequence
    }

    def getModulus(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(1).getBigInteger
    }

    def getPublicExponent(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(2).getBigInteger
    }

    def getPrivateExponent(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(3).getBigInteger
    }

    def getPrimeP(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(4).getBigInteger
    }

    def getPrimeQ(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(5).getBigInteger
    }

    def getPrimeExponentP(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(6).getBigInteger
    }

    def getPrimeExponentQ(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(7).getBigInteger
    }

    def getCrtCoefficient(encodedKey: Array[Byte]): BigInteger = {
        this.getSequence(encodedKey)(8).getBigInteger
    }
}
