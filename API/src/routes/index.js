import express from "express";
import authRouter from "./auth.js";

const router = express();

router.use("/api", authRouter);

export default router;
