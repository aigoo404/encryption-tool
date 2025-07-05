# Simple Encryption Tool

A comprehensive encryption tool designed for easy-to-use text and file protection. This tool supports multiple encryption algorithms including symmetric encryption, asymmetric encryption, and digital signatures.

## Features

- **Symmetric Encryption**: AES, DES, 3DES with multiple modes (ECB, CBC, CFB, OFB, CTR)
- **Asymmetric Encryption**: RSA encryption/decryption
- **Digital Signatures**: RSA and DSA signature generation and verification
- **Key Generation**: Generate secure keys for all supported algorithms
- **File Operations**: Encrypt/decrypt files and sign/verify file signatures
- **User-Friendly GUI**: Intuitive interface with tabbed panels

## Requirements

- Java 8 or higher
- Maven (for building from source)

## Installation

### Option 1: Run from IDE (Recommended for Development)
1. Clone this repository:
   ```bash
   git clone https://github.com/aigoo404/encryption-tool.git
   cd EncryptionTool
   ```
2. Open the project in your favorite IDE:
- IntelliJ IDEA : File → Open → Select project folder
- Eclipse : File → Import → Existing Projects into Workspace
- VS Code : File → Open Folder
- NetBeans : File → Open Project
- Or any other Java IDE of your choice
3. Run the main class: `controller.MainFrame`
### Option 2: Run Pre-built JAR
1. Clone this repository (or download the project)
2. Navigate to the out/artifacts/EncryptionTool_jar/ folder
3. Run the JAR file:
   ```
   java -jar EncryptionTool.
   jar
   ```
⚠️ Important : Make sure you have JDK installed on your computer before running the JAR file. If JDK is not installed, Windows might try to open the JAR file with WinRAR or other archive programs instead of running it as a Java application.

### Option 3: Executable File
(Executable file will be provided later)

## How to Use
### 1. Symmetric Encryption
Step 1 : Select "Symmetrical" from the encryption type dropdown

Step 2 : Choose your algorithm:

- AES : Advanced Encryption Standard (recommended)
- DES : Data Encryption Standard (legacy)
- 3DES : Triple DES (more secure than DES)

Step 3 : Configure settings:

- Mode : ECB, CBC, CFB, OFB, CTR (CBC recommended)
- Key Size :
  - AES: 128, 192, 256 bits
  - DES: 56 bits
  - 3DES: 112, 168 bits
- Padding : NoPadding, PKCS5, ISO10126

Step 4 : Generate or load keys:

- Use the "Key Generation" tab to create new keys
- Use the "Encryption/Decryption" tab to encrypt/decrypt text or files

### 2. Asymmetric Encryption (RSA)
Step 1 : Select "Asymmetrical" from the encryption type dropdown

Step 2 : Configure RSA settings:

- Key Size : 1024, 2048, 4096 bits (2048+ recommended)
- Padding : PKCS1Padding (recommended)

Step 3 : Generate key pairs:

- Use the "Key Generation" tab to create public/private key pairs
- Save keys to files for later use

Step 4 : Encrypt/Decrypt:

- Use public key for encryption
- Use private key for decryption

### 3. Digital Signatures
Step 1 : Select "Digital Signature" from the encryption type dropdown

Step 2 : Choose algorithm:

- RSA : RSA-based signatures (supports both hashed and raw data)
- DSA : Digital Signature Algorithm (automatic hashing only)

Step 3 : Choose mode:

- Sign : Create digital signatures
- Verify : Verify existing signatures 

Signing Files

1. Load Private Key : Click "Load key" and select your private key file
2. Choose Hashing Option :

   - RSA : Choose "Yes, it's already hashed" for pre-hashed files or "No, hash it for me" for regular files

   - DSA : Only "No, hash it for me" is available (automatic)

3. Select File : Click "Choose file" to select the file you want to sign

4. Generate Signature : Click "Sign it!" to create the signature

5. Save Signature :

   - "Save it to a file": Saves signature to a separate .sig file

   - "Embed it to the file": Creates a new .signed file with embedded signature
   
Verifying Signatures

1. Load Public Key : Click "Load key" and select the corresponding public key

2. Choose Hashing Option : Same as signing process

3. Select Original File : Choose the file that was signed

4. Load Signature : Click "Load Signature" and select the .sig file

5. Verify : Click "Verify!" to check signature validity

## Key Management

### Generating Keys

1. Navigate to the appropriate key generation tab

2. Select desired key size

3. Click "Generate" to create new keys

4. Save keys to secure locations using the "Save" buttons

### Key File Formats
- Private Keys : Saved in PKCS#8 format with .pem or .key extension

- Public Keys : Saved in X.509 format with .pem or .key extension

- Symmetric Keys : Saved as Base64-encoded strings

- Signatures : Saved as Base64-encoded strings with .sig extension

## Security Best Practices
1. Key Size : Use at least 2048-bit keys for RSA, 256-bit for AES

2. Key Storage : Store private keys securely and never share them

3. Algorithm Choice :

   - Use AES for symmetric encryption

   - Use RSA-2048+ or DSA for signatures

   - Avoid DES (use 3DES or AES instead)

4. Mode Selection : Use CBC or CTR mode for symmetric encryption

5. Backup : Always backup your keys securely

## File Operations

### Supported File Types

- Any file type can be encrypted/decrypted

- Text files work best for demonstration

- Binary files are fully supported

### File Naming Conventions

- Encrypted files: original.ext.encrypted

- Signed files: original.signed.ext

- Signature files: original.ext.sig

- Key files: keyname.pem or keyname.key

## Troubleshooting

### Common Issues

"Invalid key format"

- Ensure you're using the correct key type (public vs private)

- Check that the key file is not corrupted

- Verify the key was generated with compatible settings

"Signature verification failed"

- Ensure you're using the correct public key

- Check that the file hasn't been modified since signing

- Verify the signature file is complete and uncorrupted

"Encryption failed"

- Check that all required parameters are set

- Ensure sufficient disk space for output files

- Verify input file is accessible

### Getting Help

- Check that Java 8+ is installed: java -version

- Ensure all required files are in the correct locations

- Try with smaller test files first

- Check console output for detailed error messages
## Technical Details

### Supported Algorithms (as of version 1.1)
- AES : 128/192/256-bit keys, multiple modes

- DES : 56-bit keys (legacy support)

- 3DES : 112/168-bit keys

- RSA : 1024/2048/4096-bit keys

- DSA : 1024/2048/4096-bit keys
### Dependencies

- Java Cryptography Extension (JCE)

- Swing GUI framework

- Standard Java libraries