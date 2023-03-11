package com.dora.myapplication;

public class AwsRdsData {
    public static final String DATABASE_NAME = "crypto_vault";
    public static final String url = "jdbc:mysql://crypto-vault-rds-1.ce3udfzrdpxd.ap-south-1.rds.amazonaws.com:3306/" +
            DATABASE_NAME + "?autoReconnect=true&useSSL=false";
    public static final String username = "admin", password = "namitVit$83";
    public static final String TABLE_NAME_AES = "AES";
}
