// routes/auth.js
import express from "express";
import { register, login } from "../controllers/auth.js";

const authRouter = express.Router();

// Register Endpoint
authRouter.post("/auth/register", register);
authRouter.post("/auth/login", login);

export default authRouter;
